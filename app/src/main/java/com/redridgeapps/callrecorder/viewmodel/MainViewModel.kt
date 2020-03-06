package com.redridgeapps.callrecorder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redridgeapps.callrecorder.callutils.Recordings
import com.redridgeapps.repository.viewmodel.IMainViewModel
import com.redridgeapps.ui.MainUIModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val recordings: Recordings
) : ViewModel(), IMainViewModel {

    init {

        recordings.getRecordingList()
            .onEach {
                model.recordingList = it
                model.refreshing = false
            }
            .launchIn(viewModelScope)
    }

    override val model = MainUIModel()

    override fun deleteRecording(recordingId: Int) {
        model.refreshing = true
        recordings.deleteRecording(recordingId)
    }
}
