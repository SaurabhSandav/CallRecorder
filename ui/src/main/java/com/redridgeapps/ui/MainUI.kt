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
import com.redridgeapps.repository.uimodel.IMainUIModel
import com.redridgeapps.repository.viewmodel.IMainViewModel
import com.redridgeapps.ui.initialization.Destination
import com.redridgeapps.ui.initialization.UIInitializer
import com.redridgeapps.ui.utils.fetchViewModel
import javax.inject.Inject

object MainDestination : Destination {

    override val uiInitializer = MainUIInitializer::class.java
}

@Model
class MainUIModel(
    override var refreshing: Boolean = true,
    override var recordingList: List<RecordingItem> = listOf()
) : IMainUIModel

class MainUIInitializer @Inject constructor() : UIInitializer {

    @Composable
    override fun initialize() {
        val viewModel = fetchViewModel<IMainViewModel>()
        val model = MainUIModel()
        viewModel.setModel(model)
        MainUI(viewModel, model)
    }
}

@Composable
fun MainUI(viewModel: IMainViewModel, model: MainUIModel) {

    val topAppBar = @Composable {

        TopAppBar(
            title = @Composable { Text(text = stringResource(R.string.app_name)) },
            actionData = listOf("Systemization"),
            action = @Composable {

                val backStack = BackStackAmbient.current
                val onClick = { backStack.push(SystemizerDestination) }

                TextButton(contentColor = MaterialTheme.colors().onPrimary, onClick = onClick) {
                    Text(text = it)
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
