package com.redridgeapps.repository.viewmodel

interface ISettingsViewModel : ViewModelMarker {

    val uiState: Any

    fun flipSystemization()

    fun flipRecording()
}
