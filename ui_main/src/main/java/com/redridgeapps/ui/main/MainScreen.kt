package com.redridgeapps.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.redridgeapps.ui.common.routing.viewModel
import com.redridgeapps.ui.main.ui.Content

@Composable
fun MainScreen(onNavigateToSettings: OnNavigateToSettings) {

    val viewModel = viewModel<MainViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    Content(
        onNavigateToSettings = onNavigateToSettings,
        recordingListState = uiState.recordingListState,
        filterState = uiState.filterState,
        selectionState = uiState.selectionState,
        autoDeleteEnabled = uiState.autoDeleteEnabled,
        selectedRecordingOperations = uiState.selectedRecordingOperations,
        currentPlayback = uiState.currentPlayback,
    )
}
