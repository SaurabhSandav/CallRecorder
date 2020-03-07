package com.redridgeapps.ui

import androidx.compose.Composable
import androidx.compose.Model
import androidx.ui.core.Modifier
import androidx.ui.core.Text
import androidx.ui.foundation.AdapterList
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.Icon
import androidx.ui.material.*
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
    var selectedId: Int = -1
)

object MainDestination : Destination {

    @Composable
    override fun initializeUI() {

        val viewModel = fetchViewModel<IMainViewModel>()

        MainUI(viewModel)
    }
}

val IMainViewModel.mainState: MainState
    get() = uiState as MainState

@Composable
private fun MainUI(viewModel: IMainViewModel) {

    Scaffold(
        topAppBar = @Composable { MainTopAppBar() },
        bottomAppBar = getMainBottomAppBar(viewModel)
    ) { modifier ->
        ContentMain(viewModel, modifier)
    }
}

@Composable
private fun MainTopAppBar() {

    TopAppBar(
        title = @Composable { Text(text = stringResource(R.string.app_name)) },
        actions = @Composable {

            val backStack = BackStackAmbient.current
            val onClick = { backStack.push(SystemizerDestination) }

            TextButton(contentColor = MaterialTheme.colors().onPrimary, onClick = onClick) {
                Text(text = "Systemization")
            }
        }
    )
}

private fun getMainBottomAppBar(viewModel: IMainViewModel): @Composable() ((BottomAppBar.FabConfiguration?) -> Unit)? {

    val bottomAppBar = @Composable() { it: BottomAppBar.FabConfiguration? ->
        BottomAppBar(fabConfiguration = it) {
            IconButton(onClick = {}) {
                Icon(vectorResource(id = R.drawable.ic_baseline_delete_24))
            }
        }
    }

    return if (viewModel.mainState.selectedId == -1) null else bottomAppBar
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

    Box(modifier, gravity = ContentGravity.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun RecordingList(viewModel: IMainViewModel, modifier: Modifier = Modifier.None) {
    AdapterList(
        data = viewModel.mainState.recordingList,
        modifier = modifier
    ) { recordingItem -> RecordingListItem(recordingItem, viewModel) }
}

@Composable
private fun RecordingListItem(recordingItem: RecordingItem, viewModel: IMainViewModel) {
    ListItem(recordingItem.name, secondaryText = recordingItem.number) {
        viewModel.mainState.selectedId = recordingItem.id
    }
}
