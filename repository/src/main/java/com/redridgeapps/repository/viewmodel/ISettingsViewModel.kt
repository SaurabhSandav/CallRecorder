package com.redridgeapps.repository.viewmodel

import com.redridgeapps.repository.callutils.PcmChannels
import com.redridgeapps.repository.callutils.PcmEncoding
import com.redridgeapps.repository.callutils.PcmSampleRate

interface ISettingsViewModel : ViewModelMarker {

    val uiState: Any

    fun flipSystemization()

    fun flipRecording()

    fun setAudioRecordSampleRate(audioRecordSampleRate: PcmSampleRate)

    fun setAudioRecordChannels(audioRecordChannels: PcmChannels)

    fun setAudioRecordEncoding(audioRecordEncoding: PcmEncoding)
}
