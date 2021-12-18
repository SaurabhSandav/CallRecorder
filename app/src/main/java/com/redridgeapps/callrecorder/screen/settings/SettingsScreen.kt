package com.redridgeapps.callrecorder.screen.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.viewinterop.viewModel
import androidx.lifecycle.ViewModelProvider
import com.redridgeapps.callrecorder.screen.settings.ui.Content

@Composable
fun SettingsScreen(
    viewModelFactory: ViewModelProvider.Factory,
    onNavigateUp: () -> Unit,
) {

    val viewModel = viewModel<SettingsViewModel>(factory = viewModelFactory)
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
