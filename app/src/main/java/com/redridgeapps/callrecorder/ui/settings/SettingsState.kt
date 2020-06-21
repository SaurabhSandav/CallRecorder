package com.redridgeapps.callrecorder.ui.settings

import com.redridgeapps.callrecorder.callutils.recording.PcmChannels
import com.redridgeapps.callrecorder.callutils.recording.PcmEncoding
import com.redridgeapps.callrecorder.callutils.recording.PcmSampleRate
import kotlinx.coroutines.flow.Flow

class SettingsState(
    val isSystemized: Flow<Boolean>,
    val recordingEnabled: Flow<Boolean>,
    val audioRecordSampleRate: Flow<PcmSampleRate>,
    val audioRecordChannels: Flow<PcmChannels>,
    val audioRecordEncoding: Flow<PcmEncoding>,
    val recordingAutoDeleteEnabled: Flow<Boolean>,
    val recordingAutoDeleteAfterDays: Flow<Int>
)
