package com.redridgeapps.callrecorder.ui.main

import androidx.compose.*
import androidx.ui.animation.Crossfade
import androidx.ui.core.DropdownPopup
import androidx.ui.core.Modifier
import androidx.ui.core.PopupProperties
import androidx.ui.core.gesture.longPressGestureFilter
import androidx.ui.core.tag
import androidx.ui.foundation.*
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.material.*
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.*
import androidx.ui.unit.Dp
import androidx.ui.unit.dp
import com.koduok.compose.navigation.BackStackAmbient
import com.redridgeapps.callrecorder.callutils.PlaybackState
import com.redridgeapps.callrecorder.callutils.RecordingId
import com.redridgeapps.callrecorder.ui.compose_viewmodel.fetchViewModel
import com.redridgeapps.callrecorder.ui.routing.Destination
import com.redridgeapps.callrecorder.ui.settings.SettingsDestination
import com.redridgeapps.callrecorder.ui.utils.Highlight
import com.redridgeapps.callrecorder.ui.utils.ListSelection
import com.redridgeapps.callrecorder.utils.enumSetComplementOf
import com.redridgeapps.callrecorder.utils.enumSetOfAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import java.util.*

@Model
class MainState(
    var isRefreshing: Boolean = true,
    var recordingList: List<RecordingListItem> = listOf(),
    val selection: ListSelection<RecordingListItem.Entry> = ListSelection(),
    val playbackState: Flow<PlaybackState> = emptyFlow(),
    var recordingListFilterSet: EnumSet<RecordingListFilter> = enumSetOfAll()
)

sealed class RecordingListItem {

    class Divider(val title: String) : RecordingListItem()

    class Entry(
        val id: RecordingId,
        val name: String,
        val number: String,
        val overlineText: String,
        val metaText: String,
        val isStarred: Boolean
    ) : RecordingListItem()
}

enum class RecordingListFilter {
    All,
    Incoming,
    Outgoing,
    Starred;

    fun toReadableString(): String = name

    companion object {
        val EXCEPT_ALL: EnumSet<RecordingListFilter> = enumSetComplementOf(All)
    }
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

    val topAppBar = @Composable {
        when {
            viewModel.uiState.selection.inMultiSelectMode -> SelectionTopAppBar(viewModel)
            else -> MainTopAppBar(viewModel)
        }
    }

    Scaffold(
        topAppBar = topAppBar
    ) { modifier ->

        Column(modifier) {
            Box(Modifier.weight(1F)) {
                ContentMain(viewModel)
            }
            PlaybackBar(viewModel)
        }
    }
}

@Composable
private fun MainTopAppBar(viewModel: MainViewModel) {

    TopAppBar(
        title = { Text(text = "Call Recorder", modifier = Modifier.padding(bottom = 16.dp)) },
        actions = {
            IconFilter(viewModel)
            IconSettings()
        }
    )
}

@Composable
private fun SelectionTopAppBar(viewModel: MainViewModel) {

    val selectionSize = viewModel.uiState.selection.size

    TopAppBar(
        title = {
            Text(
                text = "$selectionSize selected",
                modifier = Modifier.padding(bottom = 16.dp)
            )
        },
        actions = {
            IconDelete(viewModel)
            IconCloseSelectionMode(viewModel)
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
private fun IconFilter(viewModel: MainViewModel) {

    var showFilterPopup by state { false }

    IconButton(onClick = { showFilterPopup = !showFilterPopup }) {
        Icon(Icons.Default.FilterList)
    }

    if (!showFilterPopup) return

    val popupProperties = PopupProperties(
        isFocusable = true,
        onDismissRequest = { showFilterPopup = false }
    )

    DropdownPopup(popupProperties = popupProperties) {
        Surface(border = Border(2.dp, Color.LightGray)) {
            Column {

                for ((index, option) in RecordingListFilter.values().withIndex()) {

                    val padding = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = if (index == 0) 16.dp else 0.dp,
                        bottom = 16.dp
                    )

                    Row(padding) {

                        Checkbox(
                            checked = option in viewModel.uiState.recordingListFilterSet,
                            modifier = Modifier.padding(end = 16.dp),
                            onCheckedChange = { viewModel.updateRecordingListFilter(option, it) }
                        )

                        Text(text = option.toReadableString())
                    }
                }
            }
        }
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
private fun ContentMain(viewModel: MainViewModel) {

    Crossfade(current = viewModel.uiState.isRefreshing) { isRefreshing ->

        Box(Modifier.fillMaxSize(), gravity = ContentGravity.Center) {
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

    Highlight(enabled = recordingEntry in selection) {

        val onClick = { selection.select(recordingEntry) }
        val modifier = Modifier.longPressGestureFilter { selection.multiSelect(recordingEntry) }

        ListItem(
            modifier = modifier,
            onClick = onClick,
            icon = { PlayPauseIcon(viewModel, recordingEntry.id, 45.dp) },
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
    recordingId: RecordingId,
    iconSideSize: Dp,
    modifier: Modifier = Modifier
) {

    val playbackState = viewModel.uiState.playbackState.collectAsState().value
    val recordingIsPlaying =
        playbackState is PlaybackState.NotStopped.Playing && playbackState.recording.id == recordingId.value

    val onClick = {
        when {
            recordingIsPlaying -> viewModel.pausePlayback()
            else -> viewModel.startPlayback(recordingId)
        }
    }

    IconButton(onClick, modifier = modifier) {

        val icon = when {
            recordingIsPlaying -> Icons.Default.PauseCircleOutline
            else -> Icons.Default.PlayCircleOutline
        }

        key(icon) {
            Icon(
                asset = icon.copy(defaultWidth = iconSideSize, defaultHeight = iconSideSize),
                tint = MaterialTheme.colors.secondary
            )
        }
    }
}

@Composable
private fun PlaybackBar(viewModel: MainViewModel) {

    val playbackState = viewModel.uiState.playbackState.collectAsState().value

    if (playbackState !is PlaybackState.NotStopped) return

    Surface(
        modifier = Modifier.fillMaxWidth().height(65.dp),
        color = Color.DarkGray,
        elevation = 10.dp
    ) {

        val constraintSet = ConstraintSet {
            val textTag = tag("text")
            val sliderTag = tag("slider")
            val playTag = tag("play")

            textTag.top.constrainTo(parent.top)
            textTag.bottom.constrainTo(sliderTag.top)
            textTag.left.constrainTo(parent.left)
            textTag.right.constrainTo(playTag.left)

            sliderTag.top.constrainTo(textTag.bottom)
            sliderTag.bottom.constrainTo(parent.bottom)
            sliderTag.left.constrainTo(parent.left)
            sliderTag.right.constrainTo(playTag.left)
            sliderTag.width = wrap

            playTag.constrainVerticallyTo(parent)
            playTag.right.constrainTo(parent.right)
        }

        ConstraintLayout(constraintSet, Modifier.fillMaxSize()) {

            Text(
                text = playbackState.recording.name,
                modifier = Modifier.tag("text").padding(5.dp),
                color = MaterialTheme.colors.onPrimary
            )

            var sliderPosition = 0F

            Slider(
                value = playbackState.progress.collectAsState(0F).value,
                onValueChange = { sliderPosition = it },
                onValueChangeEnd = { viewModel.setPlaybackPosition(sliderPosition) },
                modifier = Modifier.tag("slider").height(20.dp).padding(5.dp),
                color = MaterialTheme.colors.secondary
            )

            PlayPauseIcon(
                viewModel = viewModel,
                recordingId = RecordingId(playbackState.recording.id),
                iconSideSize = 40.dp,
                modifier = Modifier.tag("play").padding(10.dp)
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

            ListItem(
                text = if (selection.single().isStarred) "Unstar" else "Star",
                onClick = { viewModel.toggleStar() }
            )

            ListItem("Trim silence at start/end", onClick = { viewModel.trimSilenceEnds() })
            ListItem("Convert to Mp3", onClick = { viewModel.convertToMp3() })
            ListItem("Delete", onClick = { viewModel.deleteRecordings() })
        }
    }
}
