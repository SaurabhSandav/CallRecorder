package com.redridgeapps.ui.main.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.redridgeapps.ui.main.CurrentPlayback
import com.redridgeapps.ui.main.FilterState
import com.redridgeapps.ui.main.OnNavigateToSettings
import com.redridgeapps.ui.main.RecordingListState
import com.redridgeapps.ui.main.SelectedRecordingOperations
import com.redridgeapps.ui.main.SelectionState

@Composable
internal fun Content(
    onNavigateToSettings: OnNavigateToSettings,
    recordingListState: RecordingListState,
    filterState: FilterState,
    selectionState: SelectionState,
    selectedRecordingOperations: SelectedRecordingOperations,
    currentPlayback: CurrentPlayback?,
) {

    val topBar = @Composable {

        MainTopAppBar(
            onNavigateToSettings = onNavigateToSettings,
            filterState = filterState,
            selectionState = selectionState,
            selectedRecordingOperations = selectedRecordingOperations,
        )
    }

    val bottomBar = @Composable {

        if (currentPlayback != null)
            PlaybackBottomBar(currentPlayback = currentPlayback)
    }

    Scaffold(
        topBar = topBar,
        bottomBar = bottomBar,
    ) { innerPadding ->

        ScaffoldBody(
            recordingListState = recordingListState,
            modifier = Modifier.padding(innerPadding)
        )
    }

    val singleSelection = selectionState.selection.singleOrNull()

    if (!selectionState.inMultiSelectMode && singleSelection != null) {

        OptionsDialog(
            selectedRecording = singleSelection,
            selectedRecordingOperations = selectedRecordingOperations,
            onDismissRequest = selectionState.onCloseSelectionMode,
        )
    }
}
