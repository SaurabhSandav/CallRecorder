package com.redridgeapps.callrecorder.screen.settings.ui

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
import com.redridgeapps.callrecorder.screen.settings.OnUpdateContactNames
import com.redridgeapps.callrecorder.screen.settings.Preference
import com.redridgeapps.prefs.Prefs

@Composable
internal fun Content(
    onNavigateUp: () -> Unit,
    isAppSystemized: Preference<Boolean>,
    recordingEnabled: Preference<Boolean>,

    onUpdateContactNames: OnUpdateContactNames,

    audioRecordSampleRate: Preference<Prefs.AudioRecord.SampleRate>,
    audioRecordChannels: Preference<Prefs.AudioRecord.Channels>,
    audioRecordEncoding: Preference<Prefs.AudioRecord.Encoding>,

    autoDeleteEnabled: Preference<Boolean>,
    autoDeleteAfterDays: Preference<Int>,
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
