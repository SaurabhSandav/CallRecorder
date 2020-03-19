package com.redridgeapps.ui

import androidx.annotation.DrawableRes
import androidx.compose.Composable
import androidx.compose.Model
import androidx.ui.core.Modifier
import androidx.ui.core.Text
import androidx.ui.foundation.AdapterList
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.Icon
import androidx.ui.layout.LayoutSize
import androidx.ui.material.*
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Close
import androidx.ui.res.stringResource
import androidx.ui.res.vectorResource
import com.koduok.compose.navigation.BackStackAmbient
import com.redridgeapps.repository.RecordingItem
import com.redridgeapps.repository.viewmodel.IMainViewModel
import com.redridgeapps.ui.routing.Destination
import com.redridgeapps.ui.utils.fetchViewModel

@Model
class MainState(
    var refreshing: Boolean = true,
    var recordingList: List<RecordingItem> = listOf(),
    var selectedId: Int = -1,
    var playing: Int = -1
)

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

    val bottomAppBar =
        @Composable { it: BottomAppBar.FabConfiguration? -> MainBottomAppBar(viewModel, it) }

    Scaffold(
        topAppBar = { MainTopAppBar() },
        bottomAppBar = if (viewModel.mainState.selectedId == -1) null else bottomAppBar
    ) { modifier ->
        ContentMain(viewModel, modifier)
    }
}

@Composable
private fun MainTopAppBar() {

    TopAppBar(
        title = { Text(text = stringResource(R.string.app_name)) },
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

    BottomAppBar(fabConfiguration = fabConfiguration) {

        IconButtonPlayback(viewModel)
        IconButtonDelete(viewModel)


        Box(gravity = ContentGravity.CenterEnd) {
            IconButtonClose(viewModel)
        }
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

    if (viewModel.mainState.refreshing)
        IsRefreshing(modifier)
    else
        RecordingList(viewModel, modifier)
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
        modifier = modifier
    ) { recordingItem ->
        RecordingListItem(recordingItem, viewModel)
    }
}

@Composable
private fun RecordingListItem(recordingItem: RecordingItem, viewModel: IMainViewModel) {

    ListItem(recordingItem.name, secondaryText = recordingItem.number) {
        viewModel.mainState.selectedId = recordingItem.id
    }
}
