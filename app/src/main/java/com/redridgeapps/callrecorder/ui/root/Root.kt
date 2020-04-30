package com.redridgeapps.callrecorder.ui.root

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultRegistry
import androidx.compose.Composable
import androidx.compose.Providers
import androidx.ui.core.setContent
import androidx.ui.material.MaterialTheme
import com.koduok.compose.navigation.Router
import com.redridgeapps.callrecorder.ui.compose_viewmodel.ComposeViewModelFetcher
import com.redridgeapps.callrecorder.ui.compose_viewmodel.ComposeViewModelStores
import com.redridgeapps.callrecorder.ui.compose_viewmodel.WithViewModelStores
import com.redridgeapps.callrecorder.ui.firstrun.FirstRunDestination
import com.redridgeapps.callrecorder.ui.main.MainDestination
import com.redridgeapps.callrecorder.ui.routing.Destination
import com.redridgeapps.callrecorder.ui.utils.ActivityResultRegistryAmbient
import com.redridgeapps.callrecorder.ui.utils.ComposeViewModelStoresAmbient
import com.redridgeapps.callrecorder.ui.utils.ViewModelFetcherAmbient

fun ComponentActivity.showUI(
    isFirstRun: Boolean,
    activityResultRegistry: ActivityResultRegistry,
    composeViewModelStores: ComposeViewModelStores,
    composeViewModelFetcher: ComposeViewModelFetcher
) {

    setContent {
        val content = @Composable { Root(isFirstRun) }

        Providers(
            ActivityResultRegistryAmbient provides activityResultRegistry,
            ComposeViewModelStoresAmbient provides composeViewModelStores,
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
        WithViewModelStores {
            Router(start = destination) { currentRoute ->
                currentRoute.data.initializeUI()
            }
        }
    }
}
