package com.redridgeapps.repository.viewmodel

interface IMainViewModel : ViewModelMarker {

    val uiState: Any

    fun deleteRecording(recordingId: Int)
}
