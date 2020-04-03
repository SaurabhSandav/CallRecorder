package com.redridgeapps.repository.viewmodel

interface IMainViewModel : ViewModelMarker {

    val uiState: Any

    fun startPlayback(recordingId: Int)

    fun stopPlayback()

    fun convertToMp3()

    fun deleteSelectedRecording()
}
