package com.redridgeapps.ui

import android.app.Activity
import androidx.activity.result.ActivityResultRegistry
import androidx.compose.Composable
import androidx.compose.Providers
import androidx.lifecycle.Lifecycle
import androidx.ui.core.setContent
import androidx.ui.material.MaterialTheme
import com.koduok.compose.navigation.Router
import com.redridgeapps.repository.viewmodel.utils.IComposeViewModelStores
import com.redridgeapps.repository.viewmodel.utils.IViewModelFetcher
import com.redridgeapps.ui.routing.Destination
import com.redridgeapps.ui.utils.*

fun Activity.showUI(
    isFirstRun: Boolean,
    lifecycle: Lifecycle,
    activityResultRegistry: ActivityResultRegistry,
    composeViewModelStores: IComposeViewModelStores,
    composeViewModelFetcher: IViewModelFetcher
) {

    setContent {
        val content = @Composable { Root(isFirstRun) }

        Providers(
            LifecycleAmbient provides lifecycle,
            ActivityResultRegistryAmbient provides activityResultRegistry,
            ComposeViewModelStoresAmbient provides composeViewModelStores,
            ViewModelFetcherAmbient provides composeViewModelFetcher,
            children = content
        )
    }
}

// TODO Remove when disposition bug fixed
fun Activity.destroyUI() {
    setContent {}
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
