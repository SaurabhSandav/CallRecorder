package com.redridgeapps.ui.firstrun

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.redridgeapps.ui.common.routing.viewModel
import com.redridgeapps.ui.firstrun.ui.Content

@Composable
fun FirstRunScreen(onConfigFinished: () -> Unit) {

    val viewModel = viewModel<FirstRunViewModel>()
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
