package com.redridgeapps.ui

import androidx.compose.Composable
import androidx.ui.material.MaterialTheme
import com.koduok.compose.navigation.Router
import com.redridgeapps.ui.initialization.InitializeUI
import com.redridgeapps.ui.utils.WithViewModelStores

@Composable
fun Root(firstRun: Boolean) {

    val destination = when {
        firstRun -> SystemizerDestination
        else -> MainDestination
    }

    MaterialTheme {
        WithViewModelStores {
            Router(start = destination) { currentRoute ->
                InitializeUI(currentRoute.data.uiInitializer)
            }
        }
    }
}
