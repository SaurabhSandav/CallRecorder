package com.redridgeapps.ui.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.redridgeapps.callutils.recording.PcmChannels
import com.redridgeapps.callutils.recording.PcmEncoding
import com.redridgeapps.callutils.recording.PcmSampleRate
import com.redridgeapps.ui.settings.Preference

@Composable
internal fun SettingsBody(
    isAppSystemized: Preference<Boolean>,
    recordingEnabled: Preference<Boolean>,

    onUpdateContactNames: () -> Unit,

    audioRecordSampleRate: Preference<PcmSampleRate>,
    audioRecordChannels: Preference<PcmChannels>,
    audioRecordEncoding: Preference<PcmEncoding>,

    autoDeleteEnabled: Preference<Boolean>,
    autoDeleteAfterDays: Preference<Int>,

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
