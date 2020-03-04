package com.redridgeapps.ui

import androidx.compose.Composable
import androidx.ui.material.MaterialTheme
import com.koduok.compose.navigation.Router
import com.redridgeapps.repository.ISystemizer
import com.redridgeapps.ui.initialization.InitializeUI
import com.redridgeapps.ui.utils.WithViewModelStores

@Composable
fun Root(systemizer: ISystemizer) {

    val destination = when {
        systemizer.isAppSystemized() -> MainDestination
        else -> SystemizerDestination
    }

    MaterialTheme {
        WithViewModelStores {
            Router(start = destination) { currentRoute ->
                InitializeUI(currentRoute.data.uiInitializer)
            }
        }
    }
}
