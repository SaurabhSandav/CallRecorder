package com.redridgeapps.ui.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.viewinterop.viewModel
import androidx.lifecycle.ViewModelProvider
import com.redridgeapps.ui.main.ui.Content

@Composable
fun MainScreen(
    viewModelFactory: ViewModelProvider.Factory,
    onNavigateToSettings: OnNavigateToSettings,
) {

    val viewModel = viewModel<MainViewModel>(factory = viewModelFactory)
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
