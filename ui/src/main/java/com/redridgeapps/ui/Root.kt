package com.redridgeapps.ui

import androidx.compose.Composable
import androidx.ui.material.MaterialTheme
import com.github.zsoltk.compose.router.Router
import com.redridgeapps.repository.ISystemizer
import com.redridgeapps.ui.initialization.InitializeUI
import com.redridgeapps.ui.utils.BackStackAmbient
import com.redridgeapps.ui.utils.WithAmbients

@Composable
fun Root(systemizer: ISystemizer) {

    val route = when {
        systemizer.isAppSystemized() -> MainRoute
        else -> SystemizerRoute
    }

    MaterialTheme {
        Router(contextId = "Root", defaultRouting = route) { backStack ->
            WithAmbients(BackStackAmbient provides backStack) {
                InitializeUI(backStack.last().uiInitializer)
            }
        }
    }
}
