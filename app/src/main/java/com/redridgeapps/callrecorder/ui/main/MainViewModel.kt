package com.redridgeapps.callrecorder.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redridgeapps.callrecorder.Recording
import com.redridgeapps.callrecorder.RecordingQueries
import com.redridgeapps.callrecorder.callutils.*
import com.redridgeapps.callrecorder.services.AudioEndsTrimmingServiceLauncher
import com.redridgeapps.callrecorder.services.Mp3ConversionServiceLauncher
import com.redridgeapps.callrecorder.utils.enumSetOf
import com.redridgeapps.callrecorder.utils.launchNoJob
import com.redridgeapps.callrecorder.utils.toLocalDate
import com.redridgeapps.callrecorder.utils.toLocalDateTime
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val recordings: Recordings,
    private val recordingQueries: RecordingQueries,
    private val callPlayback: CallPlayback,
    private val mp3ConversionServiceLauncher: Mp3ConversionServiceLauncher,
    private val audioEndsTrimmingServiceLauncher: AudioEndsTrimmingServiceLauncher
) : ViewModel() {

    private val recordingListFilter = MutableStateFlow(enumSetOf(RecordingListFilter.All))

    init {
        observeRecordingList()
    }

    val uiState = MainState(callPlayback.playbackState)

    fun startPlayback(recordingId: RecordingId) = viewModelScope.launchNoJob {

        val recording = recordingQueries.get(listOf(recordingId.value)).asFlow().mapToOne().first()
        val playbackStatus = callPlayback.playbackState.first()

        when {
            playbackStatus is PlaybackState.NotStopped.Paused && playbackStatus.recording.id == recording.id -> {
                playbackStatus.resumePlayback()
            }
            else -> playbackStatus.startNewPlayback(recording)
        }
    }

    fun pausePlayback() = viewModelScope.launchNoJob {
        val playbackStatus = callPlayback.playbackState.first()

        (playbackStatus as? PlaybackState.NotStopped.Playing)?.pausePlayback()
    }

    fun setPlaybackPosition(position: Float) = viewModelScope.launchNoJob {
        val playbackStatus = callPlayback.playbackState.first()

        (playbackStatus as? PlaybackState.NotStopped)?.setPosition(position)
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

        val filterSet = EnumSet.copyOf(uiState.recordingListFilterSet)

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

        uiState.recordingListFilterSet = filterSet
        recordingListFilter.value = filterSet
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

            val metaText = durationFormat
                .format(it.duration.toHours(), it.duration.toMinutes(), it.duration.seconds)

            // Date of current recording
            val recordingDate = it.start_instant.toLocalDate()

            // Add Divider if recording was made on a new day
            if (currentDate != recordingDate) {
                resultList.add(RecordingListItem.Divider(recordingDate.format(newDayFormatter)))
                currentDate = recordingDate
            }

            // Add recording entry
            resultList.add(
                RecordingListItem.Entry(
                    id = RecordingId(it.id),
                    name = it.name,
                    number = it.number,
                    overlineText = overlineText,
                    metaText = metaText,
                    isStarred = it.is_starred
                )
            )
        }

        return resultList
    }

    private fun stopPlayback() = viewModelScope.launchNoJob {
        val playbackStatus = callPlayback.playbackState.first()

        (playbackStatus as? PlaybackState.NotStopped)?.stopPlayback()
    }
}

private val overlineFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
private val newDayFormatter = DateTimeFormatter.ofPattern("MMMM d, uuuu")
private const val durationFormat = "%d:%02d:%02d"
