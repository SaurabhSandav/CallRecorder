package com.redridgeapps.callrecorder.ui.root

import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultRegistry
import androidx.compose.Composable
import androidx.compose.Providers
import androidx.ui.core.setContent
import androidx.ui.material.MaterialTheme
import com.koduok.compose.navigation.Router
import com.redridgeapps.callrecorder.ui.firstrun.FirstRunDestination
import com.redridgeapps.callrecorder.ui.main.MainDestination
import com.redridgeapps.callrecorder.ui.routing.Destination
import com.redridgeapps.callrecorder.ui.utils.ActivityResultRegistryAmbient
import com.redridgeapps.callrecorder.ui.utils.ComposeViewModelStoresAmbient
import com.redridgeapps.callrecorder.ui.utils.ViewModelFetcherAmbient
import com.redridgeapps.callrecorder.ui.utils.WithViewModelStores
import com.redridgeapps.repository.viewmodel.utils.IComposeViewModelStores
import com.redridgeapps.repository.viewmodel.utils.IViewModelFetcher

fun ComponentActivity.showUI(
    isFirstRun: Boolean,
    activityResultRegistry: ActivityResultRegistry,
    composeViewModelStores: IComposeViewModelStores,
    composeViewModelFetcher: IViewModelFetcher
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
