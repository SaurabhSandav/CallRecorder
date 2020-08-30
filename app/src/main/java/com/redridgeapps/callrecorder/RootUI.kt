package com.redridgeapps.callrecorder

import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.ui.platform.setContent
import com.koduok.compose.navigation.Router
import com.koduok.compose.navigation.core.backStackController
import com.redridgeapps.compose.viewmodel.ComposeFramework
import com.redridgeapps.compose.viewmodel.WithViewModels
import com.redridgeapps.ui.common.routing.setupViewModel
import com.redridgeapps.ui.common.utils.ActivityResultRegistryAmbient
import com.redridgeapps.ui.firstrun.FirstRunScreen
import com.redridgeapps.ui.main.MainScreen
import com.redridgeapps.ui.settings.SettingsScreen

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
                MaterialTheme {
                    Root(isFirstRun)
                }
            }
        }
    }
}

enum class Routing {
    FirstRun,
    Main,
    Settings,
}

@Composable
fun Root(isFirstRun: Boolean) {

    val start = when {
        isFirstRun -> Routing.FirstRun
        else -> Routing.Main
    }

    Router(start = start) { currentRoute ->
        when (currentRoute.data) {
            Routing.FirstRun -> FirstRunScreen(onConfigFinished = { replace(Routing.Main) })
            Routing.Main -> MainScreen(onNavigateToSettings = { push(Routing.Settings) })
            Routing.Settings -> SettingsScreen(onNavigateUp = { pop() })
        }
    }
}
