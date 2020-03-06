package com.redridgeapps.ui

import androidx.compose.Composable
import androidx.compose.Model
import androidx.compose.state
import androidx.ui.core.Text
import androidx.ui.foundation.AdapterList
import androidx.ui.foundation.Dialog
import androidx.ui.graphics.Color
import androidx.ui.layout.Center
import androidx.ui.layout.LayoutSize
import androidx.ui.material.*
import androidx.ui.material.surface.Surface
import androidx.ui.res.stringResource
import com.koduok.compose.navigation.BackStackAmbient
import com.redridgeapps.repository.RecordingItem
import com.redridgeapps.repository.viewmodel.IMainViewModel
import com.redridgeapps.ui.initialization.Destination
import com.redridgeapps.ui.utils.fetchViewModel

@Model
class MainUIModel(
    var refreshing: Boolean = true,
    var recordingList: List<RecordingItem> = listOf()
)

object MainDestination : Destination {

    @Composable
    override fun initializeUI() {

        val viewModel = fetchViewModel<IMainViewModel>()
        val model = viewModel.model as MainUIModel

        MainUI(viewModel, model)
    }
}

@Composable
fun MainUI(viewModel: IMainViewModel, model: MainUIModel) {

    val topAppBar = @Composable {

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

    Scaffold(topAppBar = topAppBar) {
        ContentMain(viewModel, model)
    }
}

@Composable
private fun ContentMain(viewModel: IMainViewModel, model: MainUIModel) {

    var selectedItemId by state { -1 }

    if (selectedItemId > -1) {
        Dialog(onCloseRequest = { selectedItemId = -1 }) {
            Surface(color = Color.White) {
                ListItem("Delete") {
                    viewModel.deleteRecording(selectedItemId)
                    selectedItemId = -1
                }
            }
        }
    }

    if (!model.refreshing) {
        AdapterList(data = model.recordingList, modifier = LayoutSize.Fill) { recordingItem ->
            ListItem(recordingItem.name, secondaryText = recordingItem.number) {
                selectedItemId = recordingItem.id
            }
        }
    } else {
        Center {
            CircularProgressIndicator()
        }
    }
}
