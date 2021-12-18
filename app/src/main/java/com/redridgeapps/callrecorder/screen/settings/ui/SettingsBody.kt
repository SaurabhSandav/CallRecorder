package com.redridgeapps.callrecorder.screen.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.redridgeapps.callrecorder.screen.settings.Preference
import com.redridgeapps.prefs.Prefs

@Composable
internal fun SettingsBody(
    isAppSystemized: Preference<Boolean>,
    recordingEnabled: Preference<Boolean>,

    onUpdateContactNames: () -> Unit,

    audioRecordSampleRate: Preference<Prefs.AudioRecord.SampleRate>,
    audioRecordChannels: Preference<Prefs.AudioRecord.Channels>,
    audioRecordEncoding: Preference<Prefs.AudioRecord.Encoding>,

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