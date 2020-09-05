package com.redridgeapps.ui.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.redridgeapps.callutils.recording.PcmChannels
import com.redridgeapps.callutils.recording.PcmEncoding
import com.redridgeapps.callutils.recording.PcmSampleRate
import com.redridgeapps.ui.settings.PreferenceValue

@Composable
internal fun SettingsBody(
    isAppSystemized: PreferenceValue<Boolean>,
    recordingEnabled: PreferenceValue<Boolean>,

    onUpdateContactNames: () -> Unit,

    audioRecordSampleRate: PreferenceValue<PcmSampleRate>,
    audioRecordChannels: PreferenceValue<PcmChannels>,
    audioRecordEncoding: PreferenceValue<PcmEncoding>,

    autoDeleteEnabled: PreferenceValue<Boolean>,
    autoDeleteAfterDays: PreferenceValue<Int>,

    modifier: Modifier,
) {

    Column(modifier = modifier) {

        GeneralPreference(
            isAppSystemized = isAppSystemized,
            recordingEnabled = recordingEnabled,
            onUpdateContactNames = onUpdateContactNames
        )

        RecordingPreference(
            audioRecordSampleRate = audioRecordSampleRate,
            audioRecordChannels = audioRecordChannels,
            audioRecordEncoding = audioRecordEncoding,
        )

        AutoDeletePreference(
            autoDeleteEnabled = autoDeleteEnabled,
            autoDeleteAfterDays = autoDeleteAfterDays,
        )
    }
}
