package com.redridgeapps.callrecorder.ui.main

import androidx.compose.Composable
import androidx.compose.Model
import androidx.compose.frames.modelListOf
import androidx.compose.key
import androidx.ui.animation.Crossfade
import androidx.ui.core.Modifier
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
import androidx.ui.material.icons.filled.Ballot
import androidx.ui.material.icons.filled.Delete
import androidx.ui.material.icons.filled.PauseCircleOutline
import androidx.ui.material.icons.filled.PlayCircleOutline
import androidx.ui.material.icons.filled.Settings
import androidx.ui.material.icons.outlined.Ballot
import androidx.ui.text.style.TextOverflow
import androidx.ui.unit.dp
import com.koduok.compose.navigation.BackStackAmbient
import com.redridgeapps.callrecorder.ui.main.Playback.PLAYING
import com.redridgeapps.callrecorder.ui.main.Playback.STOPPED
import com.redridgeapps.callrecorder.ui.routing.Destination
import com.redridgeapps.callrecorder.ui.settings.SettingsDestination
import com.redridgeapps.callrecorder.ui.utils.Highlight
import com.redridgeapps.callrecorder.ui.utils.fetchViewModel
import com.redridgeapps.repository.viewmodel.IMainViewModel

@Model
class MainState(
    var isRefreshing: Boolean = true,
    var recordingList: List<RecordingListItem> = listOf(),
    var selectionMode: Boolean = false,
    var selection: MutableList<Int> = modelListOf(),
    var playback: Playback = STOPPED
)

sealed class RecordingListItem {

    class Divider(val title: String) : RecordingListItem()

    class Entry(
        val id: Int,
        val name: String,
        val number: String,
        val overlineText: String,
        val metaText: String
    ) : RecordingListItem()
}

sealed class Playback {
    class PLAYING(val recordingId: Int) : Playback()
    class PAUSED(val recordingId: Int) : Playback()
    object STOPPED : Playback()
}

object MainDestination : Destination {

    @Composable
    override fun initializeUI() {

        val viewModel = fetchViewModel<IMainViewModel>()

        MainUI(viewModel)
    }
}

private val IMainViewModel.mainState: MainState
    get() = uiState as MainState

@Composable
private fun MainUI(viewModel: IMainViewModel) {

    Scaffold(
        topAppBar = { MainTopAppBar(viewModel) }
    ) { modifier ->
        ContentMain(viewModel, modifier)
    }
}

@Composable
private fun MainTopAppBar(viewModel: IMainViewModel) {

    TopAppBar(
        title = { Text(text = "Call Recorder", modifier = Modifier.padding(bottom = 16.dp)) },
        actions = {

            if (viewModel.mainState.selectionMode && viewModel.mainState.selection.isNotEmpty())
                IconDelete(viewModel)

            IconSelectionMode(viewModel)
            IconSettings()
        }
    )
}

@Composable
private fun IconDelete(viewModel: IMainViewModel) {

    IconButton(onClick = { viewModel.deleteRecordings() }) {
        Icon(Icons.Default.Delete)
    }
}

@Composable
private fun IconSelectionMode(viewModel: IMainViewModel) {

    val onClick = {
        val selectionMode = viewModel.mainState.selectionMode
        if (selectionMode) viewModel.mainState.selection.clear()
        viewModel.mainState.selectionMode = !selectionMode
    }

    IconButton(onClick) {

        if (viewModel.mainState.selectionMode)
            Icon(Icons.Filled.Ballot)
        else
            Icon(Icons.Outlined.Ballot)
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
    viewModel: IMainViewModel,
    modifier: Modifier
) {

    Box(modifier + Modifier.fillMaxSize(), gravity = ContentGravity.Center) {

        Crossfade(current = viewModel.mainState.isRefreshing) { isRefreshing ->
            when {
                isRefreshing -> CircularProgressIndicator()
                else -> RecordingList(viewModel)
            }
        }
    }

    OptionsDialog(viewModel = viewModel)
}

@Composable
private fun RecordingList(viewModel: IMainViewModel) {

    AdapterList(
        data = viewModel.mainState.recordingList,
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
private fun RecordingListItem(recordingEntry: RecordingListItem.Entry, viewModel: IMainViewModel) {

    Highlight(enabled = recordingEntry.id in viewModel.mainState.selection) {

        val onClick = {
            if (viewModel.mainState.selectionMode)
                viewModel.mainState.selection.addOrRemove(recordingEntry.id)
            else
                viewModel.mainState.selection.clearAndAdd(recordingEntry.id)
        }

        ListItem(
            onClick = onClick,
            icon = { PlayPauseIcon(viewModel, recordingEntry.id) },
            secondaryText = { SingleLineText(recordingEntry.number) },
            overlineText = { SingleLineText(recordingEntry.overlineText) },
            trailing = { SingleLineText(recordingEntry.metaText) },
            text = { SingleLineText(recordingEntry.name) }
        )
    }
}

@Composable
private fun SingleLineText(text: String) {
    Text(
        text = text,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun PlayPauseIcon(
    viewModel: IMainViewModel,
    recordingId: Int
) {

    val playback = viewModel.mainState.playback
    val recordingIdIsPlaying = playback is PLAYING && playback.recordingId == recordingId

    val onClick = {

        if (recordingIdIsPlaying)
            viewModel.pausePlayback(recordingId)
        else
            viewModel.startPlayback(recordingId)
    }

    IconButton(onClick) {

        val icon = when {
            recordingIdIsPlaying -> Icons.Default.PauseCircleOutline
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
private fun OptionsDialog(viewModel: IMainViewModel) {

    val selection = viewModel.mainState.selection

    if (viewModel.mainState.selectionMode || selection.size != 1) return

    val onCloseRequest = { selection.clear() }

    Dialog(onCloseRequest = onCloseRequest) {
        Column(Modifier.drawBackground(Color.White)) {

            ListItem("Info")
            ListItem("Convert to Mp3", onClick = { viewModel.convertToMp3() })
            ListItem("Delete", onClick = { viewModel.deleteRecordings() })
        }
    }
}

private fun <T> MutableList<T>.addOrRemove(item: T) {
    if (item in this) remove(item) else add(item)
}

private fun <T> MutableList<T>.clearAndAdd(item: T) {
    clear()
    add(item)
}
