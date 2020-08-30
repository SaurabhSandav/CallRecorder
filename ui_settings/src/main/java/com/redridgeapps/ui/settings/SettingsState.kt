package com.redridgeapps.ui.settings

import androidx.compose.runtime.Stable
import com.redridgeapps.callrecorder.callutils.recording.PcmChannels
import com.redridgeapps.callrecorder.callutils.recording.PcmEncoding
import com.redridgeapps.callrecorder.callutils.recording.PcmSampleRate

@Stable
internal data class SettingsState(
    val isAppSystemized: PreferenceValue<Boolean>,

    val recordingEnabled: PreferenceValue<Boolean>,

    val onUpdateContactNames: OnUpdateContactNames,

    val audioRecordSampleRate: PreferenceValue<PcmSampleRate>,
    val audioRecordChannels: PreferenceValue<PcmChannels>,
    val audioRecordEncoding: PreferenceValue<PcmEncoding>,

    val autoDeleteEnabled: PreferenceValue<Boolean>,
    val autoDeleteAfterDays: PreferenceValue<Int>,
)

@Stable
internal data class PreferenceValue<T>(
    val value: T,
    val onChanged: (T) -> Unit,
)

internal inline infix fun <T> T.onChange(crossinline onChanged: (T) -> Unit): PreferenceValue<T> {
    return PreferenceValue(this) { onChanged(it) }
}

internal typealias OnUpdateContactNames = () -> Unit
