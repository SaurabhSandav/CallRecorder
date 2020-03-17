package com.redridgeapps.repository.viewmodel

import com.redridgeapps.repository.callutils.MediaRecorderChannel
import com.redridgeapps.repository.callutils.MediaRecorderSampleRate
import com.redridgeapps.repository.callutils.RecordingAPI

interface ISettingsViewModel : ViewModelMarker {

    val uiState: Any

    fun flipSystemization()

    fun flipRecording()

    fun setRecordingAPI(recordingAPI: RecordingAPI)

    fun setMediaRecorderChannels(mediaRecorderChannel: MediaRecorderChannel)

    fun setMediaRecorderSampleRate(mediaRecorderSampleRate: MediaRecorderSampleRate)
}
