package com.redridgeapps.ui.firstrun

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.viewinterop.viewModel
import androidx.lifecycle.ViewModelProvider
import com.redridgeapps.ui.firstrun.ui.Content

@Composable
fun FirstRunScreen(
    viewModelFactory: ViewModelProvider.Factory,
    onConfigFinished: () -> Unit,
) {

    val viewModel = viewModel<FirstRunViewModel>(factory = viewModelFactory)
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
