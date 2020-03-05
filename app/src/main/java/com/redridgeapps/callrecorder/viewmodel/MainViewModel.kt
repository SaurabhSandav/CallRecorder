package com.redridgeapps.callrecorder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redridgeapps.callrecorder.callutils.Recordings
import com.redridgeapps.repository.uimodel.IMainUIModel
import com.redridgeapps.repository.viewmodel.IMainViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val recordings: Recordings
) : ViewModel(), IMainViewModel {

    private lateinit var model: IMainUIModel

    override fun setModel(newModel: IMainUIModel) {

        model = newModel

        recordings.getRecordingList()
            .onEach {
                model.recordingList = it
                model.refreshing = false
            }
            .launchIn(viewModelScope)
    }

    override fun deleteRecording(recordingId: Int) {
        model.refreshing = true
        recordings.deleteRecording(recordingId)
    }
}
