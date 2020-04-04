package com.redridgeapps.repository.viewmodel

interface IMainViewModel : ViewModelMarker {

    val uiState: Any

    fun startPlayback()

    fun stopPlayback()

    fun convertToMp3()

    fun deleteRecordings()
}
