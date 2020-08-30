package com.redridgeapps.ui.settings.ui

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.redridgeapps.callrecorder.callutils.recording.PcmChannels
import com.redridgeapps.callrecorder.callutils.recording.PcmEncoding
import com.redridgeapps.callrecorder.callutils.recording.PcmSampleRate
import com.redridgeapps.ui.settings.OnUpdateContactNames
import com.redridgeapps.ui.settings.PreferenceValue

@Composable
internal fun Content(
    onNavigateUp: () -> Unit,
    isAppSystemized: PreferenceValue<Boolean>,
    recordingEnabled: PreferenceValue<Boolean>,

    onUpdateContactNames: OnUpdateContactNames,

    audioRecordSampleRate: PreferenceValue<PcmSampleRate>,
    audioRecordChannels: PreferenceValue<PcmChannels>,
    audioRecordEncoding: PreferenceValue<PcmEncoding>,

    autoDeleteEnabled: PreferenceValue<Boolean>,
    autoDeleteAfterDays: PreferenceValue<Int>,
) {

    Scaffold(
        topBar = { SettingsTopAppBar(onNavigateUp) }
    ) { innerPadding ->

        SettingsBody(
            isAppSystemized = isAppSystemized,
            recordingEnabled = recordingEnabled,

            onUpdateContactNames = onUpdateContactNames,

            audioRecordSampleRate = audioRecordSampleRate,
            audioRecordChannels = audioRecordChannels,
            audioRecordEncoding = audioRecordEncoding,

            autoDeleteEnabled = autoDeleteEnabled,
            autoDeleteAfterDays = autoDeleteAfterDays,

            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
private fun SettingsTopAppBar(onNavigateUp: () -> Unit) {

    TopAppBar(
        title = { Text("Settings") },
        navigationIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(Icons.Default.ArrowBack)
            }
        }
    )
}
