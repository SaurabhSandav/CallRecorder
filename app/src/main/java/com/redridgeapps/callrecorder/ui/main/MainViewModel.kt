package com.redridgeapps.callrecorder.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redridgeapps.callrecorder.Recording
import com.redridgeapps.callrecorder.callutils.*
import com.redridgeapps.callrecorder.callutils.PlaybackState.NotStopped
import com.redridgeapps.callrecorder.callutils.PlaybackState.NotStopped.Paused
import com.redridgeapps.callrecorder.callutils.PlaybackState.NotStopped.Playing
import com.redridgeapps.callrecorder.services.AudioEndsTrimmingServiceLauncher
import com.redridgeapps.callrecorder.services.Mp3ConversionServiceLauncher
import com.redridgeapps.callrecorder.utils.*
import kotlinx.coroutines.flow.*
import java.nio.file.Paths
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val recordings: Recordings,
    private val callPlayback: CallPlayback,
    private val mp3ConversionServiceLauncher: Mp3ConversionServiceLauncher,
    private val audioEndsTrimmingServiceLauncher: AudioEndsTrimmingServiceLauncher
) : ViewModel() {

    private val recordingListFilter = MutableStateFlow(enumSetOfAll<RecordingListFilter>())

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

        when {
            playbackStatus is Paused && playbackStatus.recording.id == recording.id -> {
                playbackStatus.resumePlayback()
            }
            else -> playbackStatus.startNewPlayback(recording)
        }
    }

    fun pausePlayback() = viewModelScope.launchNoJob {
        val playbackStatus = callPlayback.playbackState.first()

        (playbackStatus as? Playing)?.pausePlayback()
    }

    fun setPlaybackPosition(position: Float) = viewModelScope.launchNoJob {
        val playbackStatus = callPlayback.playbackState.first()

        (playbackStatus as? NotStopped)?.setPosition(position)
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

    fun updateRecordingListFilter(filter: RecordingListFilter, enabled: Boolean) {

        val filterSet = EnumSet.copyOf(recordingListFilter.value)

        when {
            filter == RecordingListFilter.All && enabled -> {
                filterSet.clear()
                filterSet.addAll(enumValues())
            }
            filter == RecordingListFilter.All && !enabled -> filterSet.clear()
            enabled -> filterSet.add(filter)
            else -> filterSet.remove(filter)
        }

        when {
            filterSet.containsAll(RecordingListFilter.EXCEPT_ALL) -> filterSet.add(
                RecordingListFilter.All
            )
            else -> filterSet.remove(RecordingListFilter.All)
        }

        recordingListFilter.value = filterSet
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
            .combine(recordingListFilter) { list: List<Recording>, filterSet: EnumSet<RecordingListFilter> ->
                uiState.isRefreshing = true
                filterRecordingList(list, filterSet)
            }
            .onEach {
                uiState.recordingList = prepareRecordingList(it)
                uiState.isRefreshing = false
            }
            .launchIn(viewModelScope)
    }

    private fun filterRecordingList(
        list: List<Recording>,
        filterSet: EnumSet<RecordingListFilter>
    ): List<Recording> = list.filter {
        RecordingListFilter.All in filterSet ||
                (RecordingListFilter.Incoming in filterSet && it.call_direction == CallDirection.INCOMING) ||
                (RecordingListFilter.Outgoing in filterSet && it.call_direction == CallDirection.OUTGOING) ||
                (RecordingListFilter.Starred in filterSet && it.is_starred)
    }

    private fun prepareRecordingList(recordingList: List<Recording>): List<RecordingListItem> {

        val resultList = mutableListOf<RecordingListItem>()

        // Track recordings on a given day
        var currentDate: LocalDate? = null

        recordingList.forEach {

            // Format call Interval
            val startTime = it.start_instant.toLocalDateTime().format(overlineFormatter)
            val overlineText = "$startTime • ${it.call_direction}"

            val metaText = it.duration.getFormatted()

            // Date of current recording
            val recordingDate = it.start_instant.toLocalDate()

            // Add Divider if recording was made on a new day
            if (currentDate != recordingDate) {
                resultList.add(RecordingListItem.Divider(recordingDate.format(newDayFormatter)))
                currentDate = recordingDate
            }

            // Collect applicable filters
            val filterSet = enumSetNoneOf<RecordingListFilter>()

            when (it.call_direction) {
                CallDirection.INCOMING -> filterSet.add(RecordingListFilter.Incoming)
                CallDirection.OUTGOING -> filterSet.add(RecordingListFilter.Outgoing)
            }

            if (it.is_starred)
                filterSet.add(RecordingListFilter.Starred)

            // Add recording entry
            resultList.add(
                RecordingListItem.Entry(
                    id = RecordingId(it.id),
                    name = "${it.id} - ${it.name}",
                    number = it.number,
                    overlineText = overlineText,
                    metaText = metaText,
                    applicableFilters = filterSet
                )
            )
        }

        return resultList
    }

    private fun stopPlayback() = viewModelScope.launchNoJob {
        val playbackStatus = callPlayback.playbackState.first()

        (playbackStatus as? NotStopped)?.stopPlayback()
    }
}

private val overlineFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
private val newDayFormatter = DateTimeFormatter.ofPattern("MMMM d, uuuu")
private val fullDateFormatter = DateTimeFormatter.ofPattern("MMMM d, uuuu, HH:mm:ss")

private fun Duration.getFormatted(): String =
    "%d:%02d:%02d".format(seconds / 3600, (seconds % 3600) / 60, (seconds % 60))
