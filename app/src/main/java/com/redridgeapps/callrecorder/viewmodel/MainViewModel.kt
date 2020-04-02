package com.redridgeapps.callrecorder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redridgeapps.callrecorder.Recording
import com.redridgeapps.callrecorder.callutils.CallPlayback
import com.redridgeapps.callrecorder.callutils.Recordings
import com.redridgeapps.repository.toLocalDate
import com.redridgeapps.repository.toLocalDateTime
import com.redridgeapps.repository.viewmodel.IMainViewModel
import com.redridgeapps.ui.MainState
import com.redridgeapps.ui.RecordingListItem
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val recordings: Recordings,
    private val callPlayback: CallPlayback
) : ViewModel(), IMainViewModel {

    init {

        recordings.getRecordingList()
            .onEach {
                uiState.recordingList = prepareRecordingList(it)
                uiState.isRefreshing = false
            }
            .launchIn(viewModelScope)
    }

    override val uiState = MainState()

    override fun startPlayback(recordingId: Int) {
        uiState.playingId = recordingId

        viewModelScope.launch {
            callPlayback.startPlaying(recordingId) { uiState.playingId = null }
        }
    }

    override fun stopPlayback() {
        callPlayback.stopPlaying()
        uiState.playingId = null
    }

    override fun deleteSelectedRecording() {
        uiState.isRefreshing = true
        recordings.deleteRecording(uiState.selectedId!!)
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
            val overlineText = "$startTime -> $endTime (${it.call_direction})"

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
