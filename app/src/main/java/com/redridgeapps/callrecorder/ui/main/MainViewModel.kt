package com.redridgeapps.callrecorder.ui.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redridgeapps.callrecorder.Recording
import com.redridgeapps.callrecorder.callutils.*
import com.redridgeapps.callrecorder.callutils.PlaybackState.NotStopped
import com.redridgeapps.callrecorder.callutils.PlaybackState.NotStopped.Paused
import com.redridgeapps.callrecorder.callutils.PlaybackState.NotStopped.Playing
import com.redridgeapps.callrecorder.services.AudioEndsTrimmingServiceLauncher
import com.redridgeapps.callrecorder.services.Mp3ConversionServiceLauncher
import com.redridgeapps.callrecorder.utils.humanReadableByteCount
import com.redridgeapps.callrecorder.utils.launchNoJob
import com.redridgeapps.callrecorder.utils.toLocalDate
import com.redridgeapps.callrecorder.utils.toLocalDateTime
import kotlinx.coroutines.flow.*
import java.nio.file.Paths
import java.time.Duration
import java.time.format.DateTimeFormatter
import java.util.*

class MainViewModel @ViewModelInject constructor(
    private val recordings: Recordings,
    private val callPlayback: CallPlayback,
    private val mp3ConversionServiceLauncher: Mp3ConversionServiceLauncher,
    private val audioEndsTrimmingServiceLauncher: AudioEndsTrimmingServiceLauncher
) : ViewModel() {

    private val recordingListFilter = MutableStateFlow(enumSetNoneOf<RecordingListFilter>())

    init {
        observeRecordingList()
    }

    val uiState = MainState(
        playbackState = callPlayback.playbackState,
        recordingListFilter = recordingListFilter
    )

    fun startPlayback(recordingId: RecordingId) = viewModelScope.launchNoJob {

        val recording = recordings.getRecording(recordingId).first()
        val playbackStatus = callPlayback.playbackState.first()

        with(callPlayback) {
            when {
                playbackStatus is Paused && playbackStatus.recording.id == recording.id -> {
                    playbackStatus.resumePlayback()
                }
                else -> playbackStatus.startNewPlayback(recording)
            }
        }
    }

    fun pausePlayback() = viewModelScope.launchNoJob {
        val playbackStatus = callPlayback.playbackState.first()

        with(callPlayback) {
            (playbackStatus as? Playing)?.pausePlayback()
        }
    }

    fun setPlaybackPosition(position: Float) = viewModelScope.launchNoJob {
        val playbackStatus = callPlayback.playbackState.first()

        with(callPlayback) {
            (playbackStatus as? NotStopped)?.setPosition(position)
        }
    }

    fun toggleStar() = viewModelScope.launchNoJob {
        recordings.toggleStar(uiState.selection.map { it.id })
        uiState.selection.clear()
    }

    fun trimSilenceEnds() = viewModelScope.launchNoJob {
        audioEndsTrimmingServiceLauncher.launch(uiState.selection.map { it.id })
        uiState.selection.clear()
    }

    fun convertToMp3() = viewModelScope.launchNoJob {
        mp3ConversionServiceLauncher.launch(uiState.selection.map { it.id })
        uiState.selection.clear()
    }

    fun deleteRecordings() = viewModelScope.launchNoJob {
        recordings.deleteRecording(uiState.selection.map { it.id })
        uiState.selection.clear()
    }

    fun toggleRecordingListFilter(filter: RecordingListFilter) {

        val filterSet = EnumSet.copyOf(recordingListFilter.value)

        when (filter) {
            in filterSet -> filterSet.remove(filter)
            else -> filterSet.add(filter)
        }

        recordingListFilter.value = filterSet
    }

    fun clearRecordingListFilters() {
        recordingListFilter.value = enumSetNoneOf()
    }

    suspend fun getSelectionInfo(): List<Pair<String, String>> {

        val selection = uiState.selection.single()
        val wavData = recordings.getWavData(selection.id)
        val recording = recordings.getRecording(selection.id).first()

        return buildList {

            val saveFile = Paths.get(recording.save_path).fileName.toString()
            add("File Name: " to saveFile)

            add("Contact Name: " to recording.name)
            add("Number: " to recording.number)

            val formattedStartTime =
                fullDateFormatter.format(recording.start_instant.toLocalDateTime())
            add("Recording Started: " to formattedStartTime)

            add("Duration: " to recording.duration.getFormatted())

            val direction = when (recording.call_direction) {
                CallDirection.INCOMING -> "Incoming"
                CallDirection.OUTGOING -> "Outgoing"
            }
            add("Direction: " to direction)

            val encoding = when (wavData.bitsPerSample.asPcmEncoding()) {
                PcmEncoding.PCM_8BIT -> "Pcm 8 bit"
                PcmEncoding.PCM_16BIT -> "Pcm 16 bit"
                PcmEncoding.PCM_FLOAT -> "Pcm 32 bit (Float)"
            }
            add("Pcm Encoding: " to encoding)

            add("Sample Rate: " to "${wavData.sampleRate.value} Hz")
            add("Channels: " to if (wavData.channels.value == 1) "Mono" else "Stereo")
            add("Bitrate: " to "${wavData.bitRate.toLong()} kb/s")
            add("File Size: " to humanReadableByteCount(wavData.fileSize.toLong()))
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopPlayback()
    }

    private fun observeRecordingList() {

        recordings.getRecordingList()
            .map {
                uiState.isRefreshing = true
                it.asRecordingMapByDate()
            }
            .combine(recordingListFilter) { recordingMapByDate, filterSet ->

                val filteredMap = filterRecordingMap(recordingMapByDate, filterSet)

                uiState.recordingList = filteredMap.flatMap { listOf(it.key) + it.value }
                uiState.isRefreshing = false
            }
            .launchIn(viewModelScope)
    }

    private fun filterRecordingMap(
        recordingMapByDate: Map<RecordingListItem.Divider, List<RecordingListItem.Entry>>,
        filterSet: EnumSet<RecordingListFilter>
    ): Map<RecordingListItem.Divider, List<RecordingListItem.Entry>> = when {
        filterSet.isEmpty() -> recordingMapByDate
        else -> {
            recordingMapByDate.mapValues { entry ->
                entry.value.filter { recording -> recording.applicableFilters.any { it in filterSet } }
            }.filter { entry -> entry.value.isNotEmpty() }
        }
    }

    private fun List<Recording>.asRecordingMapByDate(): Map<RecordingListItem.Divider, List<RecordingListItem.Entry>> {
        return groupBy { it.start_instant.toLocalDate() }
            .mapKeys { RecordingListItem.Divider(it.key.format(newDayFormatter)) }
            .mapValues { entry ->
                entry.value.map {

                    // Format call Interval
                    val startTime = it.start_instant.toLocalDateTime().format(overlineFormatter)
                    val overlineText = "$startTime • ${it.call_direction}"

                    val metaText = it.duration.getFormatted()

                    // Collect applicable filters
                    val filterSet = enumSetNoneOf<RecordingListFilter>()

                    when (it.call_direction) {
                        CallDirection.INCOMING -> filterSet.add(RecordingListFilter.Incoming)
                        CallDirection.OUTGOING -> filterSet.add(RecordingListFilter.Outgoing)
                    }

                    if (it.is_starred)
                        filterSet.add(RecordingListFilter.Starred)

                    // Mapped recording entry
                    RecordingListItem.Entry(
                        id = RecordingId(it.id),
                        name = "${it.id} - ${it.name}",
                        number = it.number,
                        overlineText = overlineText,
                        metaText = metaText,
                        applicableFilters = filterSet
                    )
                }
            }
    }

    private fun stopPlayback() = viewModelScope.launchNoJob {
        val playbackStatus = callPlayback.playbackState.first()

        with(callPlayback) {
            (playbackStatus as? NotStopped)?.stopPlayback()
        }
    }
}

private val overlineFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
private val newDayFormatter = DateTimeFormatter.ofPattern("MMMM d, uuuu")
private val fullDateFormatter = DateTimeFormatter.ofPattern("MMMM d, uuuu, HH:mm:ss")

private fun Duration.getFormatted(): String =
    "%d:%02d:%02d".format(seconds / 3600, (seconds % 3600) / 60, (seconds % 60))

private inline fun <reified T : Enum<T>> enumSetNoneOf(): EnumSet<T> {
    return EnumSet.noneOf(T::class.java)
}