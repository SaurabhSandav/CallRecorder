package com.redridgeapps.callrecorder.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redridgeapps.callrecorder.Recording
import com.redridgeapps.callrecorder.callutils.CallPlayback
import com.redridgeapps.callrecorder.callutils.PlaybackState
import com.redridgeapps.callrecorder.callutils.RecordingId
import com.redridgeapps.callrecorder.callutils.Recordings
import com.redridgeapps.callrecorder.utils.launchNoJob
import com.redridgeapps.callrecorder.utils.toLocalDate
import com.redridgeapps.callrecorder.utils.toLocalDateTime
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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

    val uiState = MainState(playbackState = callPlayback.playbackState)

    fun startPlayback(recordingId: RecordingId) = viewModelScope.launchNoJob {

        val playbackStatus = callPlayback.playbackState.first()

        if (playbackStatus is PlaybackState.Paused && playbackStatus.recordingId == recordingId) {
            callPlayback.resumePlayback()
        } else {
            callPlayback.startPlayback(recordingId)
        }
    }

    fun pausePlayback() {
        callPlayback.pausePlayback()
    }

    fun stopPlayback() {
        callPlayback.stopPlayback()
    }

    fun updateContactName() = viewModelScope.launchNoJob {
        uiState.selection.forEach { recordings.updateContactName(it.id) }
        uiState.selection.clear()
    }

    fun convertToMp3() = viewModelScope.launchNoJob {
        uiState.selection.forEach { recordings.convertToMp3(it.id) }
        uiState.selection.clear()
    }

    fun deleteRecordings() = viewModelScope.launchNoJob {
        uiState.selection.forEach { recordings.deleteRecording(it.id) }
        uiState.selection.clear()
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
