package com.redridgeapps.repository.viewmodel

import com.redridgeapps.repository.uimodel.IMainUIModel

interface IMainViewModel : ViewModelMarker {

    fun setModel(newModel: IMainUIModel)

    fun deleteRecording(recordingId: Int)
}
