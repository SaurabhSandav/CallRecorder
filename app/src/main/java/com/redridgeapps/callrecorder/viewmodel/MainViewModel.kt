package com.redridgeapps.callrecorder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redridgeapps.callrecorder.callutils.CallPlayback
import com.redridgeapps.callrecorder.callutils.Recordings
import com.redridgeapps.repository.viewmodel.IMainViewModel
import com.redridgeapps.ui.MainState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val recordings: Recordings,
    private val callPlayback: CallPlayback
) : ViewModel(), IMainViewModel {

    init {

        recordings.getRecordingList()
            .onEach {
                uiState.recordingList = it
                uiState.refreshing = false
            }
            .launchIn(viewModelScope)
    }

    override val uiState = MainState()

    override fun startPlayback(recordingId: Int) {
        uiState.playing = recordingId

        viewModelScope.launch {
            callPlayback.startPlaying(recordingId) { uiState.playing = -1 }
        }
    }

    override fun stopPlayback() {
        callPlayback.stopPlaying()
        uiState.playing = -1
    }

    override fun deleteSelectedRecording() {
        uiState.refreshing = true
        recordings.deleteRecording(uiState.selectedId)
    }

    override fun onCleared() {
        super.onCleared()
        callPlayback.releasePlayer()
    }
}
