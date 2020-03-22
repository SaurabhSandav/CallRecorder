package com.redridgeapps.ui

import androidx.annotation.DrawableRes
import androidx.compose.Composable
import androidx.compose.Model
import androidx.ui.animation.Crossfade
import androidx.ui.core.Modifier
import androidx.ui.core.Text
import androidx.ui.foundation.AdapterList
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.Icon
import androidx.ui.layout.Column
import androidx.ui.layout.LayoutPadding
import androidx.ui.layout.LayoutSize
import androidx.ui.layout.LayoutWidth
import androidx.ui.material.*
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Close
import androidx.ui.res.vectorResource
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
        topAppBar = { MainTopAppBar() },
        bottomAppBar = { MainBottomAppBar(viewModel, it) }
    ) { modifier ->
        ContentMain(viewModel, modifier)
    }
}

@Composable
private fun MainTopAppBar() {

    TopAppBar(
        title = { Text(text = "Call Recorder") },
        actions = {

            val backStack = BackStackAmbient.current
            val onClick = { backStack.push(SettingsDestination) }

            TextButton(contentColor = MaterialTheme.colors().onPrimary, onClick = onClick) {
                Text(text = "Settings")
            }
        }
    )
}

@Composable
private fun MainBottomAppBar(
    viewModel: IMainViewModel,
    fabConfiguration: BottomAppBar.FabConfiguration?
) {

    if (viewModel.mainState.selectedId == -1) return

    BottomAppBar(fabConfiguration = fabConfiguration) {

        IconButtonPlayback(viewModel)
        IconButtonDelete(viewModel)
        IconButtonClose(viewModel)
    }
}

@Composable
private fun IconButtonPlayback(viewModel: IMainViewModel) {

    @DrawableRes val drawableResId: Int
    val onClick: () -> Unit

    if (viewModel.mainState.playing == -1) {
        drawableResId = R.drawable.ic_baseline_play_arrow_24
        onClick = { viewModel.startPlayback(viewModel.mainState.selectedId) }
    } else {
        drawableResId = R.drawable.ic_baseline_stop_24
        onClick = { viewModel.stopPlayback() }
    }

    IconButton(onClick) {
        Icon(vectorResource(id = drawableResId))
    }
}

@Composable
private fun IconButtonDelete(viewModel: IMainViewModel) {

    val onClick = {
        viewModel.deleteSelectedRecording()
        viewModel.mainState.selectedId = -1
    }

    IconButton(onClick) {
        Icon(vectorResource(id = R.drawable.ic_baseline_delete_24))
    }
}

@Composable
private fun IconButtonClose(viewModel: IMainViewModel) {

    val onClick = { viewModel.mainState.selectedId = -1 }

    IconButton(onClick) {
        Icon(Icons.Default.Close)
    }
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
}

@Composable
private fun IsRefreshing(modifier: Modifier = Modifier.None) {

    Box(modifier + LayoutSize.Fill, gravity = ContentGravity.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun RecordingList(viewModel: IMainViewModel, modifier: Modifier = Modifier.None) {

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
