package com.redridgeapps.repository.viewmodel

import com.redridgeapps.repository.callutils.AudioRecordChannels
import com.redridgeapps.repository.callutils.AudioRecordEncoding
import com.redridgeapps.repository.callutils.AudioRecordSampleRate

interface ISettingsViewModel : ViewModelMarker {

    val uiState: Any

    fun flipSystemization()

    fun flipRecording()

    fun setAudioRecordSampleRate(audioRecordSampleRate: AudioRecordSampleRate)

    fun setAudioRecordChannels(audioRecordChannels: AudioRecordChannels)

    fun setAudioRecordEncoding(audioRecordEncoding: AudioRecordEncoding)
}
