package com.redridgeapps.ui

import androidx.compose.Composable
import androidx.ui.material.MaterialTheme
import com.redridgeapps.repository.ISystemizer
import com.redridgeapps.ui.router.RouterContent

@Composable
fun Root(systemizer: ISystemizer) {

    val route = when {
        systemizer.isAppSystemized() -> MainRoute
        else -> SystemizerRoute
    }

    MaterialTheme {
        RouterContent(route)
    }
}
