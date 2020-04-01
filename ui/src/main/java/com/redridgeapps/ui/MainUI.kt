package com.redridgeapps.ui

import androidx.compose.Composable
import androidx.compose.Model
import androidx.ui.animation.Crossfade
import androidx.ui.core.Modifier
import androidx.ui.core.Text
import androidx.ui.foundation.*
import androidx.ui.graphics.Color
import androidx.ui.layout.Column
import androidx.ui.layout.LayoutPadding
import androidx.ui.layout.LayoutSize
import androidx.ui.layout.LayoutWidth
import androidx.ui.material.*
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Settings
import androidx.ui.unit.dp
import com.koduok.compose.navigation.BackStackAmbient
import com.redridgeapps.repository.viewmodel.IMainViewModel
import com.redridgeapps.ui.routing.Destination
import com.redridgeapps.ui.utils.fetchViewModel

@Model
class MainState(
    var isRefreshing: Boolean = true,
    var recordingList: List<RecordingListItem> = listOf(),
    var selectedId: Int = -1,
    var playing: Int = -1
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
        topAppBar = { MainTopAppBar() }
    ) { modifier ->
        ContentMain(viewModel, modifier)
    }
}

@Composable
private fun MainTopAppBar() {

    TopAppBar(
        title = { Text(text = "Call Recorder", modifier = LayoutPadding(bottom = 16.dp)) },
        actions = {
            val backStack = BackStackAmbient.current

            IconButton(onClick = { backStack.push(SettingsDestination) }) {
                Icon(Icons.Default.Settings)
            }
        }
    )
}

@Composable
private fun ContentMain(
    viewModel: IMainViewModel,
    modifier: Modifier
) {

    Crossfade(current = viewModel.mainState.isRefreshing) { isRefreshing ->
        if (isRefreshing)
            IsRefreshing(modifier)
        else
            RecordingList(viewModel, modifier)
    }

    OptionsDialog(viewModel = viewModel)
}

@Composable
private fun IsRefreshing(modifier: Modifier) {

    Box(modifier + LayoutSize.Fill, gravity = ContentGravity.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun RecordingList(viewModel: IMainViewModel, modifier: Modifier) {

    AdapterList(
        data = viewModel.mainState.recordingList,
        modifier = modifier + LayoutSize.Fill
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
            modifier = LayoutPadding(start = 10.dp, end = 10.dp),
            color = MaterialTheme.colors().onSurface.copy(alpha = 0.12F)
        )

        Box(LayoutWidth.Fill + LayoutPadding(5.dp), gravity = ContentGravity.Center) {
            Text(dateText, style = MaterialTheme.typography().subtitle1)
        }

        Divider(
            modifier = LayoutPadding(start = 10.dp, end = 10.dp),
            color = MaterialTheme.colors().onSurface.copy(alpha = 0.12F)
        )
    }
}

@Composable
private fun RecordingListItem(recordingEntry: RecordingListItem.Entry, viewModel: IMainViewModel) {

    ListItem(
        text = recordingEntry.name,
        secondaryText = recordingEntry.number,
        overlineText = recordingEntry.overlineText,
        metaText = recordingEntry.metaText
    ) {
        viewModel.mainState.selectedId = recordingEntry.id
    }
}

@Composable
private fun OptionsDialog(viewModel: IMainViewModel) {

    if (viewModel.mainState.selectedId == -1) return

    val onCloseRequest = { viewModel.mainState.selectedId = -1 }

    Dialog(onCloseRequest = onCloseRequest) {
        Column(DrawBackground(Color.White)) {

            // TODO Move to separate Player
            if (viewModel.mainState.playing == -1)
                ListItem("Play") { viewModel.startPlayback(viewModel.mainState.selectedId) }
            else
                ListItem("Stop") { viewModel.stopPlayback() }

            ListItem("Info")

            ListItem("Delete") {
                viewModel.deleteSelectedRecording()
                viewModel.mainState.selectedId = -1
            }
        }
    }
}
