package com.redridgeapps.callrecorder.screen.firstrun

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.redridgeapps.callrecorder.screen.firstrun.ui.Content

@Composable
fun FirstRunScreen(
    onConfigFinished: () -> Unit,
) {

    val viewModel = hiltViewModel<FirstRunViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isConfigFinished) onConfigFinished()

    Content(
        isAppSystemized = uiState.isAppSystemized,
        onAppSystemize = uiState.onAppSystemize,

        allPermissionsGranted = uiState.allPermissionsGranted,
        onPermissionsResult = uiState.onPermissionsResult,

        captureAudioOutputPermissionGranted = uiState.captureAudioOutputPermissionGranted,
    )
}
