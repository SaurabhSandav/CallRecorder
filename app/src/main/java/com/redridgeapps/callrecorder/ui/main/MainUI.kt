package com.redridgeapps.callrecorder.ui.main

import androidx.compose.Composable
import androidx.compose.Model
import androidx.compose.collectAsState
import androidx.compose.key
import androidx.ui.animation.Crossfade
import androidx.ui.core.Modifier
import androidx.ui.core.gesture.longPressGestureFilter
import androidx.ui.foundation.AdapterList
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.Dialog
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.foundation.drawBackground
import androidx.ui.graphics.Color
import androidx.ui.layout.Column
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.material.CircularProgressIndicator
import androidx.ui.material.Divider
import androidx.ui.material.IconButton
import androidx.ui.material.ListItem
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Scaffold
import androidx.ui.material.TopAppBar
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Close
import androidx.ui.material.icons.filled.Delete
import androidx.ui.material.icons.filled.PauseCircleOutline
import androidx.ui.material.icons.filled.PlayCircleOutline
import androidx.ui.material.icons.filled.Settings
import androidx.ui.unit.dp
import com.koduok.compose.navigation.BackStackAmbient
import com.redridgeapps.callrecorder.callutils.PlaybackState
import com.redridgeapps.callrecorder.callutils.RecordingId
import com.redridgeapps.callrecorder.ui.compose_viewmodel.fetchViewModel
import com.redridgeapps.callrecorder.ui.routing.Destination
import com.redridgeapps.callrecorder.ui.settings.SettingsDestination
import com.redridgeapps.callrecorder.ui.utils.Highlight
import com.redridgeapps.callrecorder.ui.utils.ListSelection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Model
class MainState(
    var isRefreshing: Boolean = true,
    var recordingList: List<RecordingListItem> = listOf(),
    val selection: ListSelection<RecordingId> = ListSelection(),
    val playbackState: Flow<PlaybackState> = emptyFlow()
)

sealed class RecordingListItem {

    class Divider(val title: String) : RecordingListItem()

    class Entry(
        val id: RecordingId,
        val name: String,
        val number: String,
        val overlineText: String,
        val metaText: String
    ) : RecordingListItem()
}

object MainDestination : Destination {

    @Composable
    override fun initializeUI() {

        val viewModel = fetchViewModel<MainViewModel>()

        MainUI(viewModel)
    }
}

@Composable
private fun MainUI(viewModel: MainViewModel) {

    Scaffold(
        topAppBar = { MainTopAppBar(viewModel) }
    ) { modifier ->
        ContentMain(viewModel, modifier)
    }
}

@Composable
private fun MainTopAppBar(viewModel: MainViewModel) {

    TopAppBar(
        title = { Text(text = "Call Recorder", modifier = Modifier.padding(bottom = 16.dp)) },
        actions = {

            when {
                viewModel.uiState.selection.inMultiSelectMode -> {
                    IconDelete(viewModel)
                    IconCloseSelectionMode(viewModel)
                }
                else -> IconSettings()
            }
        }
    )
}

@Composable
private fun IconDelete(viewModel: MainViewModel) {

    IconButton(onClick = { viewModel.deleteRecordings() }) {
        Icon(Icons.Default.Delete)
    }
}

@Composable
private fun IconCloseSelectionMode(viewModel: MainViewModel) {

    val onClick = { viewModel.uiState.selection.clear() }

    IconButton(onClick) {
        Icon(Icons.Default.Close)
    }
}

@Composable
private fun IconSettings() {

    val backStack = BackStackAmbient.current

    IconButton(onClick = { backStack.push(SettingsDestination) }) {
        Icon(Icons.Default.Settings)
    }
}

@Composable
private fun ContentMain(
    viewModel: MainViewModel,
    modifier: Modifier
) {

    Crossfade(current = viewModel.uiState.isRefreshing) { isRefreshing ->

        Box(modifier + Modifier.fillMaxSize(), gravity = ContentGravity.Center) {
            when {
                isRefreshing -> CircularProgressIndicator()
                else -> RecordingList(viewModel)
            }
        }
    }

    OptionsDialog(viewModel = viewModel)
}

@Composable
private fun RecordingList(viewModel: MainViewModel) {

    AdapterList(
        data = viewModel.uiState.recordingList,
        modifier = Modifier.fillMaxSize()
    ) { recordingListItem ->

        when (recordingListItem) {
            is RecordingListItem.Divider -> RecordingListDateDivider(dateText = recordingListItem.title)
            is RecordingListItem.Entry -> RecordingListItem(recordingListItem, viewModel)
        }
    }
}

@Composable
private fun RecordingListDateDivider(dateText: String) {

    Column {

        Divider(
            modifier = Modifier.padding(start = 10.dp, end = 10.dp),
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.12F)
        )

        Box(Modifier.fillMaxWidth().padding(5.dp), gravity = ContentGravity.Center) {
            Text(dateText, style = MaterialTheme.typography.subtitle1)
        }

        Divider(
            modifier = Modifier.padding(start = 10.dp, end = 10.dp),
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.12F)
        )
    }
}

@Composable
private fun RecordingListItem(recordingEntry: RecordingListItem.Entry, viewModel: MainViewModel) {

    val selection = viewModel.uiState.selection

    Highlight(enabled = recordingEntry.id in selection) {

        val onClick = { selection.select(recordingEntry.id) }
        val modifier = Modifier.longPressGestureFilter { selection.multiSelect(recordingEntry.id) }

        ListItem(
            modifier = modifier,
            onClick = onClick,
            icon = { PlayPauseIcon(viewModel, recordingEntry.id) },
            secondaryText = { Text(recordingEntry.number) },
            overlineText = { Text(recordingEntry.overlineText) },
            trailing = { Text(recordingEntry.metaText) },
            text = { Text(recordingEntry.name) }
        )
    }
}

@Composable
private fun PlayPauseIcon(
    viewModel: MainViewModel,
    recordingId: RecordingId
) {

    val playbackState = viewModel.uiState.playbackState.collectAsState().value
    val recordingIsPlaying =
        playbackState is PlaybackState.Playing && playbackState.recordingId == recordingId

    val onClick = {
        when {
            recordingIsPlaying -> viewModel.pausePlayback()
            else -> viewModel.startPlayback(recordingId)
        }
    }

    IconButton(onClick) {

        val icon = when {
            recordingIsPlaying -> Icons.Default.PauseCircleOutline
            else -> Icons.Default.PlayCircleOutline
        }

        key(icon) {
            Icon(
                asset = icon.copy(defaultWidth = 40.dp, defaultHeight = 40.dp),
                tint = MaterialTheme.colors.secondary
            )
        }
    }
}

@Composable
private fun OptionsDialog(viewModel: MainViewModel) {

    val selection = viewModel.uiState.selection

    if (selection.inMultiSelectMode || selection.isEmpty()) return

    val onCloseRequest = { selection.clear() }

    Dialog(onCloseRequest = onCloseRequest) {
        Column(Modifier.drawBackground(Color.White)) {

            ListItem("Info")
            ListItem("Convert to Mp3", onClick = { viewModel.convertToMp3() })
            ListItem("Delete", onClick = { viewModel.deleteRecordings() })
        }
    }
}
