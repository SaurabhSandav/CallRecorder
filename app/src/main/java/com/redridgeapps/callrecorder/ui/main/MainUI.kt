package com.redridgeapps.callrecorder.ui.main

import androidx.compose.*
import androidx.ui.animation.Crossfade
import androidx.ui.core.Modifier
import androidx.ui.foundation.*
import androidx.ui.foundation.lazy.LazyColumnItems
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.material.*
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.*
import androidx.ui.text.AnnotatedString
import androidx.ui.text.SpanStyle
import androidx.ui.text.annotatedString
import androidx.ui.text.font.FontWeight
import androidx.ui.text.style.TextAlign
import androidx.ui.text.withStyle
import androidx.ui.unit.Dp
import androidx.ui.unit.dp
import com.koduok.compose.navigation.BackStackAmbient
import com.redridgeapps.callrecorder.callutils.playback.PlaybackState.NotStopped
import com.redridgeapps.callrecorder.callutils.playback.PlaybackState.NotStopped.Playing
import com.redridgeapps.callrecorder.ui.prefcomponents.SwitchPreference
import com.redridgeapps.callrecorder.ui.routing.Destination
import com.redridgeapps.callrecorder.ui.routing.viewModel
import com.redridgeapps.callrecorder.ui.settings.SettingsDestination
import com.redridgeapps.callrecorder.ui.utils.drawScrim

object MainDestination : Destination {

    @Composable
    override fun initializeUI() {

        val viewModel = viewModel<MainViewModel>()

        MainUI(viewModel)
    }
}

@Composable
private fun MainUI(viewModel: MainViewModel) {

    val topBar = @Composable {
        when {
            viewModel.uiState.selection.inMultiSelectMode -> SelectionTopAppBar(viewModel)
            else -> MainTopAppBar(viewModel)
        }
    }

    Scaffold(
        topBar = topBar
    ) { innerPadding ->

        Column(Modifier.padding(innerPadding)) {
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

    var expanded by state { false }

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

            val filterSet by viewModel.uiState.recordingListFilter.collectAsState()
            val onClick = { viewModel.toggleRecordingListFilter(filter) }

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

        DropdownMenuItem(onClick = { viewModel.clearRecordingListFilters() }) {
            Text(
                text = "Clear Filters",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
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

    LazyColumnItems(
        items = viewModel.uiState.recordingList,
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

    var modifier = Modifier.clickable(
        onClick = { selection.select(recordingEntry.id) },
        onLongClick = { selection.multiSelect(recordingEntry.id) }
    )

    if (recordingEntry.id in selection) {
        modifier = modifier.drawScrim()
    }

    viewModel.uiState.playbackState.collectAsState().value.let {
        if (it is NotStopped && it.recording.id == recordingEntry.id) {
            modifier = modifier.drawScrim(MaterialTheme.colors.secondary)
        }
    }

    ListItem(
        modifier = modifier,
        icon = { PlayPauseIcon(viewModel, recordingEntry.id, 45.dp) },
        secondaryText = { Text(recordingEntry.number) },
        overlineText = { Text(recordingEntry.overlineText) },
        trailing = { Text(recordingEntry.metaText) },
        text = { Text(recordingEntry.name) }
    )
}

@Composable
private fun PlayPauseIcon(
    viewModel: MainViewModel,
    recordingId: Long,
    iconSideSize: Dp,
    modifier: Modifier = Modifier
) {

    val recordingIsPlaying = viewModel.uiState.playbackState.collectAsState().value.let {
        it is Playing && it.recording.id == recordingId
    }

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
                onValueChangeEnd = { viewModel.setPlaybackPosition(sliderPosition) },
                modifier = Modifier.constrainAs(slider) {
                    top.linkTo(name.bottom)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(playbackIcon.start)
                    width = Dimension.fillToConstraints
                    height = Dimension.value(20.dp)
                }.padding(5.dp),
                color = MaterialTheme.colors.secondary
            )

            PlayPauseIcon(
                viewModel = viewModel,
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
private fun OptionsDialog(viewModel: MainViewModel) {

    val selection = viewModel.uiState.selection

    if (selection.inMultiSelectMode || selection.isEmpty()) return

    val onCloseRequest = { selection.clear() }

    Dialog(onCloseRequest = onCloseRequest) {

        // Occasionally calling `selection.single()` will crash.
        // Possible reason for crash: Dialog having separate composition.
        // Issue: https://issuetracker.google.com/154369470
        // Rechecking selection status inside Dialog should avoid the crash.
        if (selection.inMultiSelectMode || selection.isEmpty()) return@Dialog

        Column(Modifier.drawBackground(Color.White)) {

            var selectedIndex by state { 0 }

            TabRow(
                items = OptionsDialogTab.values().asList(),
                selectedIndex = selectedIndex
            ) { tabIndex: Int, tab: OptionsDialogTab ->

                Tab(
                    text = { Text(tab.toReadableString()) },
                    selected = selectedIndex == tabIndex,
                    onSelected = { selectedIndex = tabIndex }
                )
            }

            var recordingInfo by state<List<AnnotatedString>> { emptyList() }

            launchInComposition {
                recordingInfo = annotateRecordingInfo(viewModel)
            }

            Crossfade(selectedIndex) { tabIndex ->
                VerticalScroller {
                    when (tabIndex) {
                        0 -> OptionsDialogOptionsTab(viewModel)
                        1 -> OptionsDialogInfoTab(recordingInfo)
                    }
                }
            }
        }
    }
}

private suspend fun annotateRecordingInfo(viewModel: MainViewModel): List<AnnotatedString> {

    val infoPairs = viewModel.getSelectionInfo()

    return infoPairs.map {
        annotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(it.first) }
            append(it.second)
        }
    }
}

@Composable
private fun OptionsDialogOptionsTab(viewModel: MainViewModel) {

    Column {

        val isStarred by viewModel.getSelectionIsStarred().collectAsState(initial = null)
        SwitchPreference(
            text = "Star",
            checked = isStarred,
            onCheckedChange = { viewModel.toggleStar() }
        )

        if (viewModel.uiState.recordingAutoDeleteEnabled.collectAsState(initial = false).value) {

            val skipAutoDelete by viewModel.getSelectionSkipAutoDelete()
                .collectAsState(initial = null)

            SwitchPreference(
                text = "Skip auto delete",
                checked = skipAutoDelete,
                onCheckedChange = { viewModel.toggleSkipAutoDelete() }
            )
        }

        ListItem("Trim silence at start/end", onClick = { viewModel.trimSilenceEnds() })
        ListItem("Convert to Mp3", onClick = { viewModel.convertToMp3() })
        ListItem("Delete", onClick = { viewModel.deleteRecordings() })
    }
}

@Composable
private fun OptionsDialogInfoTab(recordingInfo: List<AnnotatedString>) {
    recordingInfo.forEach {
        ListItem(text = { Text(it) })
    }
}
