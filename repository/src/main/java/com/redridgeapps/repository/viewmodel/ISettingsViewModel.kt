package com.redridgeapps.repository.viewmodel

import com.redridgeapps.repository.callutils.RecordingAPI

interface ISettingsViewModel : ViewModelMarker {

    val uiState: Any

    fun flipSystemization()

    fun flipRecording()

    fun setRecordingAPI(recordingAPI: RecordingAPI)
}
