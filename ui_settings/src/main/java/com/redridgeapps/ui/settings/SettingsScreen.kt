package com.redridgeapps.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.redridgeapps.ui.common.routing.viewModel
import com.redridgeapps.ui.settings.ui.Content

@Composable
fun SettingsScreen(onNavigateUp: () -> Unit) {

    val viewModel = viewModel<SettingsViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    Content(
        onNavigateUp = onNavigateUp,

        isAppSystemized = uiState.isAppSystemized,
        recordingEnabled = uiState.recordingEnabled,

        onUpdateContactNames = uiState.onUpdateContactNames,

        audioRecordSampleRate = uiState.audioRecordSampleRate,
        audioRecordChannels = uiState.audioRecordChannels,
        audioRecordEncoding = uiState.audioRecordEncoding,

        autoDeleteEnabled = uiState.autoDeleteEnabled,
        autoDeleteAfterDays = uiState.autoDeleteAfterDays,
    )
}
