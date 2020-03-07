package com.redridgeapps.callrecorder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redridgeapps.callrecorder.callutils.Recordings
import com.redridgeapps.repository.viewmodel.IMainViewModel
import com.redridgeapps.ui.MainState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val recordings: Recordings
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

    override fun deleteRecording(recordingId: Int) {
        uiState.refreshing = true
        recordings.deleteRecording(recordingId)
    }
}
