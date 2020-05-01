package com.redridgeapps.callrecorder.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redridgeapps.callrecorder.Recording
import com.redridgeapps.callrecorder.callutils.CallPlayback
import com.redridgeapps.callrecorder.callutils.Recordings
import com.redridgeapps.callrecorder.ui.main.Playback.PAUSED
import com.redridgeapps.callrecorder.ui.main.Playback.PLAYING
import com.redridgeapps.callrecorder.ui.main.Playback.STOPPED
import com.redridgeapps.callrecorder.utils.launchNoJob
import com.redridgeapps.callrecorder.utils.toLocalDate
import com.redridgeapps.callrecorder.utils.toLocalDateTime
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val recordings: Recordings,
    private val callPlayback: CallPlayback
) : ViewModel() {

    init {

        recordings.getRecordingList()
            .onEach {
                uiState.recordingList = prepareRecordingList(it)
                uiState.isRefreshing = false
            }
            .launchIn(viewModelScope)
    }

    val uiState = MainState()

    fun startPlayback(recordingId: Int) = viewModelScope.launchNoJob {

        val playbackStatus = uiState.playback

        if (playbackStatus is PAUSED && playbackStatus.recordingId == recordingId) {
            uiState.playback = PLAYING(recordingId)
            callPlayback.resumePlayback()
            return@launchNoJob
        } else {

            if (playbackStatus is PLAYING)
                stopPlayback()

            uiState.playback = PLAYING(recordingId)
            callPlayback.startPlayback(recordingId) { uiState.playback = STOPPED }
        }
    }

    fun pausePlayback(recordingId: Int) {
        uiState.playback = PAUSED(recordingId)
        callPlayback.pausePlayback()
    }

    fun stopPlayback() {
        uiState.playback = STOPPED
        callPlayback.stopPlayback()
    }

    fun convertToMp3() = viewModelScope.launchNoJob {
        recordings.convertToMp3(uiState.selection.single())
        uiState.selection.clear()
    }

    fun deleteRecordings() = viewModelScope.launchNoJob {
        uiState.selection.forEach {
            recordings.deleteRecording(it)
        }
        uiState.selection.clear()
        uiState.selectionMode = true
    }

    override fun onCleared() {
        super.onCleared()
        callPlayback.releasePlayer()
    }

    private fun prepareRecordingList(recordingList: List<Recording>): List<RecordingListItem> {

        val resultList = mutableListOf<RecordingListItem>()

        // Track recordings on a given day
        var currentDate: LocalDate? = null

        recordingList.forEach {

            // Format call Interval
            val startTime = it.start_instant.toLocalDateTime().format(overlineFormatter)
            val endTime = it.end_instant.toLocalDateTime().format(overlineFormatter)
            val overlineText = "$startTime -> $endTime • ${it.call_direction}"

            // Calculate and format call duration
            val duration = Duration.between(it.start_instant, it.end_instant)
            val metaText = durationFormat
                .format(duration.toHours(), duration.toMinutes(), duration.seconds)

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
                    id = it.id,
                    name = it.name,
                    number = it.number,
                    overlineText = overlineText,
                    metaText = metaText
                )
            )
        }

        return resultList
    }
}

private val overlineFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
private val newDayFormatter = DateTimeFormatter.ofPattern("MMMM d, uuuu")
private const val durationFormat = "%d:%02d:%02d"
