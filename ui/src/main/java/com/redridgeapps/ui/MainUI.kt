package com.redridgeapps.ui

import androidx.compose.Composable
import androidx.ui.core.Text
import androidx.ui.foundation.AdapterList
import androidx.ui.layout.LayoutSize
import androidx.ui.material.*
import androidx.ui.res.stringResource
import com.redridgeapps.repository.RecordingItem
import com.redridgeapps.ui.router.NavigateTo
import com.redridgeapps.ui.router.Route
import com.redridgeapps.ui.utils.BackStackAmbient
import com.redridgeapps.ui.utils.UIInitializer
import javax.inject.Inject

object MainRoute : Route {

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
                val onClick = { NavigateTo(backStack, SystemizerRoute) }

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
    AdapterList(data = list, modifier = LayoutSize.Fill) {
        ListItem(it.name, secondaryText = it.type)
    }
}
