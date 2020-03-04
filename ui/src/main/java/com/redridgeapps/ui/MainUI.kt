package com.redridgeapps.ui

import androidx.compose.Composable
import androidx.compose.state
import androidx.ui.core.Text
import androidx.ui.foundation.AdapterList
import androidx.ui.foundation.Dialog
import androidx.ui.graphics.Color
import androidx.ui.layout.LayoutSize
import androidx.ui.material.*
import androidx.ui.material.surface.Surface
import androidx.ui.res.stringResource
import com.koduok.compose.navigation.BackStackAmbient
import com.redridgeapps.repository.RecordingItem
import com.redridgeapps.ui.initialization.Destination
import com.redridgeapps.ui.initialization.UIInitializer
import javax.inject.Inject

object MainDestination : Destination {

    override val uiInitializer = MainUIInitializer::class.java
}

class MainUIInitializer @Inject constructor(
    private val list: List<RecordingItem>
) : UIInitializer {

    @Composable
    override fun initialize() {
        MainUI(list)
    }
}

@Composable
fun MainUI(list: List<RecordingItem>) {

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
        ContentMain(list)
    }
}

@Composable
fun ContentMain(list: List<RecordingItem>) {

    var selectedItemId by state { -1 }

    if (selectedItemId > -1) {
        Dialog(onCloseRequest = { selectedItemId = -1 }) {
            Surface(color = Color.White) {
                ListItem("Delete")
            }
        }
    }

    AdapterList(data = list, modifier = LayoutSize.Fill) { recordingItem ->
        ListItem(recordingItem.name, secondaryText = recordingItem.number) {
            selectedItemId = recordingItem.id
        }
    }
}
