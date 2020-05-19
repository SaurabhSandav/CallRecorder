package com.redridgeapps.callrecorder.ui.settings

import com.redridgeapps.callrecorder.callutils.PcmChannels
import com.redridgeapps.callrecorder.callutils.PcmEncoding
import com.redridgeapps.callrecorder.callutils.PcmSampleRate
import kotlinx.coroutines.flow.Flow

class SettingsState(
    val isSystemized: Flow<Boolean>,
    val isRecordingOn: Flow<Boolean>,
    val audioRecordSampleRate: Flow<PcmSampleRate>,
    val audioRecordChannels: Flow<PcmChannels>,
    val audioRecordEncoding: Flow<PcmEncoding>
)