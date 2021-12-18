package com.redridgeapps.callrecorder.screen.main.ui

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.redridgeapps.callrecorder.screen.main.FilterState
import com.redridgeapps.callrecorder.screen.main.OnNavigateToSettings
import com.redridgeapps.callrecorder.screen.main.RecordingListFilter
import com.redridgeapps.callrecorder.screen.main.SelectedRecordingOperations
import com.redridgeapps.callrecorder.screen.main.SelectionState

@Composable
internal fun MainTopAppBar(
    onNavigateToSettings: OnNavigateToSettings,
    filterState: FilterState,
    selectionState: SelectionState,
    selectedRecordingOperations: SelectedRecordingOperations,
) {
    when {
        selectionState.inMultiSelectMode -> SelectionTopAppBar(
            selectionState = selectionState,
            selectedRecordingOperations = selectedRecordingOperations,
        )
        else -> RegularTopAppBar(
            onNavigateToSettings = onNavigateToSettings,
            filterState = filterState,
        )
    }
}

@Composable
private fun RegularTopAppBar(
    onNavigateToSettings: OnNavigateToSettings,
    filterState: FilterState,
) {

    TopAppBar(
        title = { Text("CallRec Companion") },
        actions = {

            IconFilter(filterState = filterState)

            IconSettings(onNavigateToSettings = onNavigateToSettings)
        }
    )
}

@Composable
private fun IconFilter(filterState: FilterState) {

    var expanded by remember { mutableStateOf(false) }

    val iconButton = @Composable {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.FilterList)
        }
    }

    DropdownMenu(
        toggle = iconButton,
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {

        for (filter in RecordingListFilter.values()) {

            val onClick = { filterState.onToggleFilter(filter) }

            DropdownMenuItem(onClick = onClick) {
                Row {
                    Checkbox(
                        checked = filter in filterState.filters,
                        modifier = Modifier.padding(end = 16.dp),
                        onCheckedChange = { onClick() }
                    )

                    Text(text = filter.toReadableString())
                }
            }
        }

        DropdownMenuItem(onClick = filterState.onClearRecordingListFilters) {
            Text(
                text = "Clear Filters",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun RecordingListFilter.toReadableString(): String = when (this) {
    RecordingListFilter.INCOMING -> "Incoming"
    RecordingListFilter.OUTGOING -> "Outgoing"
    RecordingListFilter.STARRED -> "Starred"
}

@Composable
private fun IconSettings(onNavigateToSettings: () -> Unit) {

    IconButton(onClick = onNavigateToSettings) {
        Icon(Icons.Default.Settings)
    }
}

@Composable
private fun SelectionTopAppBar(
    selectionState: SelectionState,
    selectedRecordingOperations: SelectedRecordingOperations,
) {

    TopAppBar(
        title = { Text("${selectionState.selection.size} selected") },
        actions = {

            IconDelete(onDeleteRecordings = selectedRecordingOperations.onDeleteRecordings)

            IconCloseSelectionMode(onCloseSelectionMode = selectionState.onCloseSelectionMode)
        }
    )
}

@Composable
private fun IconDelete(onDeleteRecordings: () -> Unit) {

    IconButton(onClick = onDeleteRecordings) {
        Icon(Icons.Default.Delete)
    }
}

@Composable
private fun IconCloseSelectionMode(onCloseSelectionMode: () -> Unit) {

    IconButton(onClick = onCloseSelectionMode) {
        Icon(Icons.Default.Close)
    }
}
