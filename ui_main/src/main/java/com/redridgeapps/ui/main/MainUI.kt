package com.redridgeapps.ui.main

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Box
import androidx.compose.foundation.ContentGravity
import androidx.compose.foundation.Icon
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ConstraintLayout
import androidx.compose.foundation.layout.Dimension
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.Checkbox
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.IconButton
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Slider
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.PauseCircleOutline
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.launchInComposition
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.annotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.redridgeapps.callrecorder.callutils.playback.PlaybackState.NotStopped
import com.redridgeapps.callrecorder.callutils.playback.PlaybackState.NotStopped.Playing
import com.redridgeapps.ui.common.pref.SwitchPreference
import com.redridgeapps.ui.common.routing.viewModel
import com.redridgeapps.ui.main.viewmodels.PlaybackViewModel
import com.redridgeapps.ui.main.viewmodels.RecordingListViewModel
import com.redridgeapps.ui.main.viewmodels.SelectionViewModel

@Composable
fun MainScreen(onNavigateToSettings: () -> Unit) {

    val recordingListViewModel = viewModel<RecordingListViewModel>()
    val playbackViewModel = viewModel<PlaybackViewModel>()
    val selectionViewModel = viewModel<SelectionViewModel>()

    val topBar = @Composable {
        when {
            selectionViewModel.selection.inMultiSelectMode -> SelectionTopAppBar(selectionViewModel)
            else -> MainTopAppBar(recordingListViewModel, onNavigateToSettings)
        }
    }

    Scaffold(
        topBar = topBar
    ) { innerPadding ->

        Column(Modifier.padding(innerPadding)) {
            Box(Modifier.weight(1F)) {
                ContentMain(recordingListViewModel, playbackViewModel, selectionViewModel)
            }
            PlaybackBar(playbackViewModel)
        }
    }
}

@Composable
private fun MainTopAppBar(
    recordingListViewModel: RecordingListViewModel,
    onNavigateToSettings: () -> Unit
) {

    TopAppBar(
        title = { Text("Call Recorder") },
        actions = {
            IconFilter(recordingListViewModel)
            IconSettings(onNavigateToSettings)
        }
    )
}

@Composable
private fun SelectionTopAppBar(selectionViewModel: SelectionViewModel) {

    val selectionSize = selectionViewModel.selection.size

    TopAppBar(
        title = { Text("$selectionSize selected") },
        actions = {
            IconDelete(selectionViewModel)
            IconCloseSelectionMode(selectionViewModel)
        }
    )
}

@Composable
private fun IconDelete(selectionViewModel: SelectionViewModel) {

    IconButton(onClick = { selectionViewModel.deleteRecordings() }) {
        Icon(Icons.Default.Delete)
    }
}

@Composable
private fun IconCloseSelectionMode(selectionViewModel: SelectionViewModel) {

    val onClick = { selectionViewModel.selection.clear() }

    IconButton(onClick) {
        Icon(Icons.Default.Close)
    }
}

@Composable
private fun IconFilter(recordingListViewModel: RecordingListViewModel) {

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

            val filterSet by recordingListViewModel.uiState.recordingListFilter.collectAsState()
            val onClick = { recordingListViewModel.toggleRecordingListFilter(filter) }

            DropdownMenuItem(onClick = onClick) {
                Row {
                    Checkbox(
                        checked = filter in filterSet,
                        modifier = Modifier.padding(end = 16.dp),
                        onCheckedChange = { onClick() }
                    )

                    Text(text = filter.toReadableString())
                }
            }
        }

        DropdownMenuItem(onClick = { recordingListViewModel.clearRecordingListFilters() }) {
            Text(
                text = "Clear Filters",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun IconSettings(onNavigateToSettings: () -> Unit) {

    IconButton(onClick = onNavigateToSettings) {
        Icon(Icons.Default.Settings)
    }
}

@Composable
private fun ContentMain(
    recordingListViewModel: RecordingListViewModel,
    playbackViewModel: PlaybackViewModel,
    selectionViewModel: SelectionViewModel
) {

    Crossfade(current = recordingListViewModel.uiState.isRefreshing) { isRefreshing ->

        Box(Modifier.fillMaxSize(), gravity = ContentGravity.Center) {
            when {
                isRefreshing -> CircularProgressIndicator()
                else -> RecordingList(recordingListViewModel, playbackViewModel, selectionViewModel)
            }
        }
    }

    OptionsDialog(selectionViewModel)
}

@Composable
private fun RecordingList(
    recordingListViewModel: RecordingListViewModel,
    playbackViewModel: PlaybackViewModel,
    selectionViewModel: SelectionViewModel
) {

    LazyColumnFor(
        items = recordingListViewModel.uiState.recordingList,
        modifier = Modifier.fillMaxSize()
    ) { recordingListItem ->

        when (recordingListItem) {
            is RecordingListItem.Divider -> RecordingListDateDivider(dateText = recordingListItem.title)
            is RecordingListItem.Entry -> RecordingListItem(
                recordingListItem,
                playbackViewModel,
                selectionViewModel
            )
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
private fun RecordingListItem(
    recordingEntry: RecordingListItem.Entry,
    playbackViewModel: PlaybackViewModel,
    selectionViewModel: SelectionViewModel
) {

    val selection = selectionViewModel.selection

    var modifier = Modifier.clickable(
        onClick = { selection.select(recordingEntry.id) },
        onLongClick = { selection.multiSelect(recordingEntry.id) }
    )

    if (recordingEntry.id in selection) {
        modifier = modifier.background(MaterialTheme.colors.onSurface.copy(alpha = SCRIM_ALPHA))
    }

    playbackViewModel.playbackState.collectAsState().value.let {
        if (it is NotStopped && it.recording.id == recordingEntry.id) {
            modifier =
                modifier.background(MaterialTheme.colors.secondary.copy(alpha = SCRIM_ALPHA))
        }
    }

    ListItem(
        modifier = modifier,
        icon = { PlayPauseIcon(playbackViewModel, recordingEntry.id, 45.dp) },
        secondaryText = { Text(recordingEntry.number) },
        overlineText = { Text(recordingEntry.overlineText) },
        trailing = { Text(recordingEntry.metaText) },
        text = { Text(recordingEntry.name) }
    )
}

@Composable
private fun PlayPauseIcon(
    playbackViewModel: PlaybackViewModel,
    recordingId: Long,
    iconSideSize: Dp,
    modifier: Modifier = Modifier
) {

    val recordingIsPlaying = playbackViewModel.playbackState.collectAsState().value.let {
        it is Playing && it.recording.id == recordingId
    }

    val onClick = {
        when {
            recordingIsPlaying -> playbackViewModel.pausePlayback()
            else -> playbackViewModel.startPlayback(recordingId)
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
private fun PlaybackBar(playbackViewModel: PlaybackViewModel) {

    val playbackState = playbackViewModel.playbackState.collectAsState().value

    if (playbackState !is NotStopped) return

    Surface(
        modifier = Modifier.fillMaxWidth().height(65.dp),
        color = Color.DarkGray,
        elevation = 10.dp
    ) {

        ConstraintLayout(Modifier.fillMaxSize()) {

            val (name, slider, playbackIcon) = createRefs()

            Text(
                text = "${playbackState.recording.id} - ${playbackState.recording.name}",
                modifier = Modifier.constrainAs(name) {
                    top.linkTo(parent.top)
                    bottom.linkTo(slider.top)
                    start.linkTo(parent.start)
                    end.linkTo(playbackIcon.start)
                }.padding(5.dp),
                color = MaterialTheme.colors.onPrimary
            )

            var sliderPosition = 0F

            Slider(
                value = playbackState.progress.collectAsState(0F).value,
                onValueChange = { sliderPosition = it },
                onValueChangeEnd = { playbackViewModel.setPlaybackPosition(sliderPosition) },
                modifier = Modifier.constrainAs(slider) {
                    top.linkTo(name.bottom)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(playbackIcon.start)
                    width = Dimension.fillToConstraints
                    height = Dimension.value(20.dp)
                }.padding(5.dp)
            )

            PlayPauseIcon(
                playbackViewModel = playbackViewModel,
                recordingId = playbackState.recording.id,
                iconSideSize = 40.dp,
                modifier = Modifier.constrainAs(playbackIcon) {
                    centerVerticallyTo(parent)
                    end.linkTo(parent.end)
                }.padding(10.dp)
            )
        }
    }
}

@Composable
private fun OptionsDialog(selectionViewModel: SelectionViewModel) {

    val selection = selectionViewModel.selection

    if (selection.inMultiSelectMode || selection.isEmpty()) return

    val onCloseRequest = { selection.clear() }

    Dialog(onDismissRequest = onCloseRequest) {

        // Occasionally calling `selection.single()` will crash.
        // Possible reason for crash: Dialog having separate composition.
        // Issue: https://issuetracker.google.com/154369470
        // Rechecking selection status inside Dialog should avoid the crash.
        if (selection.inMultiSelectMode || selection.isEmpty()) return@Dialog

        Column(Modifier.background(Color.White)) {

            var selectedIndex by remember { mutableStateOf(0) }

            TabRow(selectedTabIndex = selectedIndex) {

                for ((tabIndex, tab) in OptionsDialogTab.values().asList().withIndex()) {

                    Tab(
                        selected = selectedIndex == tabIndex,
                        onClick = { selectedIndex = tabIndex }
                    ) {
                        Text(tab.toReadableString())
                    }
                }
            }

            var recordingInfo by remember { mutableStateOf<List<AnnotatedString>>(emptyList()) }

            launchInComposition {
                recordingInfo = annotateRecordingInfo(selectionViewModel)
            }

            Crossfade(selectedIndex) { tabIndex ->
                ScrollableColumn {
                    when (tabIndex) {
                        0 -> OptionsDialogOptionsTab(selectionViewModel)
                        1 -> OptionsDialogInfoTab(recordingInfo)
                    }
                }
            }
        }
    }
}

private suspend fun annotateRecordingInfo(selectionViewModel: SelectionViewModel): List<AnnotatedString> {

    val infoPairs = selectionViewModel.getInfo()

    return infoPairs.map {
        annotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(it.first) }
            append(it.second)
        }
    }
}

@Composable
private fun OptionsDialogOptionsTab(selectionViewModel: SelectionViewModel) {

    Column {

        val isStarred by selectionViewModel.getIsStarred().collectAsState(null)
        SwitchPreference(
            text = "Star",
            checked = isStarred,
            onCheckedChange = { selectionViewModel.toggleStar() }
        )

        if (selectionViewModel.showSkipAutoDelete.collectAsState(false).value) {

            val skipAutoDelete by selectionViewModel.getSkipAutoDelete().collectAsState(null)

            SwitchPreference(
                text = "Skip auto delete",
                checked = skipAutoDelete,
                onCheckedChange = { selectionViewModel.toggleSkipAutoDelete() }
            )
        }

        ListItem(
            modifier = Modifier.clickable(onClick = { selectionViewModel.trimSilenceEnds() }),
            text = { Text(text = "Trim silence at start/end") }
        )

        ListItem(
            modifier = Modifier.clickable(onClick = { selectionViewModel.convertToMp3() }),
            text = { Text(text = "Convert to Mp3") }
        )

        ListItem(
            modifier = Modifier.clickable(onClick = { selectionViewModel.deleteRecordings() }),
            text = { Text(text = "Delete") }
        )
    }
}

@Composable
private fun OptionsDialogInfoTab(recordingInfo: List<AnnotatedString>) {
    recordingInfo.forEach {
        ListItem(text = { Text(it) })
    }
}

private const val SCRIM_ALPHA = 0.32F
