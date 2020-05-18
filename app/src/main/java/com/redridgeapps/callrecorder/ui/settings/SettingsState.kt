package com.redridgeapps.callrecorder.ui.settings

import androidx.compose.getValue
import androidx.compose.mutableStateOf
import androidx.compose.setValue
import com.redridgeapps.callrecorder.callutils.PcmChannels
import com.redridgeapps.callrecorder.callutils.PcmEncoding
import com.redridgeapps.callrecorder.callutils.PcmSampleRate

class SettingsState {

    var isSystemized by mutableStateOf<Boolean?>(null)

    var isRecordingOn by mutableStateOf<Boolean?>(null)

    var audioRecordSampleRate by mutableStateOf<PcmSampleRate?>(null)

    var audioRecordChannels by mutableStateOf<PcmChannels?>(null)

    var audioRecordEncoding by mutableStateOf<PcmEncoding?>(null)
}