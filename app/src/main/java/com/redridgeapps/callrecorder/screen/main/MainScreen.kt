package com.redridgeapps.callrecorder.screen.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.redridgeapps.callrecorder.screen.main.ui.Content

@Composable
fun MainScreen(
    onNavigateToSettings: OnNavigateToSettings,
) {

    val viewModel = hiltViewModel<MainViewModel>()
    val uiState by viewModel.uiState.collectAsState()

    Content(
        onNavigateToSettings = onNavigateToSettings,
        recordingListState = uiState.recordingListState,
        filterState = uiState.filterState,
        selectionState = uiState.selectionState,
        selectedRecordingOperations = uiState.selectedRecordingOperations,
        currentPlayback = uiState.currentPlayback,
    )
}
