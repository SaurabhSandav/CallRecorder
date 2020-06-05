package com.redridgeapps.callrecorder.ui.root

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultRegistry
import androidx.compose.Composable
import androidx.compose.Providers
import androidx.compose.key
import androidx.ui.core.setContent
import androidx.ui.material.MaterialTheme
import com.koduok.compose.navigation.Router
import com.redridgeapps.callrecorder.ui.compose_viewmodel.ComposeViewModelFetcher
import com.redridgeapps.callrecorder.ui.firstrun.FirstRunDestination
import com.redridgeapps.callrecorder.ui.main.MainDestination
import com.redridgeapps.callrecorder.ui.routing.Destination
import com.redridgeapps.callrecorder.ui.utils.ActivityResultRegistryAmbient
import com.redridgeapps.callrecorder.ui.utils.ViewModelFetcherAmbient

fun ComponentActivity.showUI(
    isFirstRun: Boolean,
    activityResultRegistry: ActivityResultRegistry,
    composeViewModelFetcher: ComposeViewModelFetcher
) {

    setContent {
        val content = @Composable { Root(isFirstRun) }

        Providers(
            ActivityResultRegistryAmbient provides activityResultRegistry,
            ViewModelFetcherAmbient provides composeViewModelFetcher,
            children = content
        )
    }
}

@Composable
fun Root(isFirstRun: Boolean) {

    val destination: Destination = when {
        isFirstRun -> FirstRunDestination
        else -> MainDestination
    }

    MaterialTheme {
        Router(start = destination) { currentRoute ->
            key(currentRoute) {
                currentRoute.data.initializeUI()
            }
        }
    }
}
