package com.redridgeapps.callrecorder.ui.root

import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.compose.Composable
import androidx.compose.Providers
import androidx.compose.key
import androidx.ui.core.setContent
import androidx.ui.material.MaterialTheme
import com.koduok.compose.navigation.Router
import com.koduok.compose.navigation.core.backStackController
import com.redridgeapps.callrecorder.ui.compose_viewmodel.ComposeFramework
import com.redridgeapps.callrecorder.ui.compose_viewmodel.setupViewModel
import com.redridgeapps.callrecorder.ui.firstrun.FirstRunDestination
import com.redridgeapps.callrecorder.ui.main.MainDestination
import com.redridgeapps.callrecorder.ui.routing.Destination
import com.redridgeapps.callrecorder.ui.utils.ActivityResultRegistryAmbient
import com.redridgeapps.callrecorder.ui.utils.ViewModelFetcherAmbient

fun ComponentActivity.setupCompose(isFirstRun: Boolean) {

    val composeFramework by viewModels<ComposeFramework>()

    composeFramework.setupViewModel()
    composeFramework.setupSavedState(this@setupCompose)

    // Handle back pressed
    onBackPressedDispatcher.addCallback(this@setupCompose) {
        if (!backStackController.pop()) {
            isEnabled = false
            this@setupCompose.onBackPressed()
            isEnabled = true
        }
    }

    setContent {
        val content = @Composable { Root(isFirstRun) }

        Providers(
            ActivityResultRegistryAmbient provides activityResultRegistry,
            ViewModelFetcherAmbient provides composeFramework.viewModelFetcher,
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
