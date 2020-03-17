package com.redridgeapps.repository.viewmodel

import com.redridgeapps.repository.callutils.*

interface ISettingsViewModel : ViewModelMarker {

    val uiState: Any

    fun flipSystemization()

    fun flipRecording()

    fun setRecordingAPI(recordingAPI: RecordingAPI)

    fun setMediaRecorderChannels(mediaRecorderChannels: MediaRecorderChannels)

    fun setMediaRecorderSampleRate(mediaRecorderSampleRate: MediaRecorderSampleRate)

    fun setAudioRecordSampleRate(audioRecordSampleRate: AudioRecordSampleRate)

    fun setAudioRecordChannels(audioRecordChannels: AudioRecordChannels)

    fun setAudioRecordEncoding(audioRecordEncoding: AudioRecordEncoding)
}
