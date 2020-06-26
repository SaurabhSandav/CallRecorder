package com.redridgeapps.callrecorder

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
import com.redridgeapps.compose.viewmodel.ComposeFramework
import com.redridgeapps.compose.viewmodel.WithViewModels
import com.redridgeapps.ui.common.routing.Destination
import com.redridgeapps.ui.common.routing.setupViewModel
import com.redridgeapps.ui.common.utils.ActivityResultRegistryAmbient
import com.redridgeapps.ui.firstrun.FirstRunDestination
import com.redridgeapps.ui.main.MainDestination

fun ComponentActivity.setupCompose(isFirstRun: Boolean) {

    val composeFramework by viewModels<ComposeFramework>()

    composeFramework.setupViewModel()

    // Handle back pressed
    onBackPressedDispatcher.addCallback(this@setupCompose) {
        if (!backStackController.pop()) {
            isEnabled = false
            this@setupCompose.onBackPressed()
            isEnabled = true
        }
    }

    setContent {

        Providers(ActivityResultRegistryAmbient provides activityResultRegistry) {
            WithViewModels(composeFramework) {
                Root(isFirstRun)
            }
        }
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
