package com.redridgeapps.callrecorder

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LifecycleOwnerAmbient
import androidx.compose.ui.platform.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import androidx.savedstate.SavedStateRegistryOwner
import com.redridgeapps.callrecorder.screen.firstrun.FirstRunScreen
import com.redridgeapps.callrecorder.screen.main.MainScreen
import com.redridgeapps.callrecorder.screen.settings.SettingsScreen
import com.redridgeapps.callrecorder.theme.CallRecorderTheme
import com.redridgeapps.common.viewmodel.AppSavedStateViewModelFactory
import com.redridgeapps.common.viewmodel.ViewModelAssistedFactoryMap

fun ComponentActivity.setupCompose(
    isFirstRun: Boolean,
    viewModelAssistedFactories: ViewModelAssistedFactoryMap,
) {

    setContent {
        CallRecorderTheme {
            Root(isFirstRun, viewModelAssistedFactories)
        }
    }
}

enum class Routing {
    FirstRun,
    Main,
    Settings,
}

@Composable
fun Root(
    isFirstRun: Boolean,
    viewModelAssistedFactories: ViewModelAssistedFactoryMap,
) {

    val start = when {
        isFirstRun -> Routing.FirstRun
        else -> Routing.Main
    }

    val navController = rememberNavController()

    NavHost(navController, startDestination = start.toString()) {

        composable(Routing.FirstRun.toString()) {

            FirstRunScreen(
                rememberViewModelFactory(viewModelAssistedFactories),
                onConfigFinished = {
                    navController.popBackStack()
                    navController.navigate(Routing.Main.toString())
                }
            )
        }

        composable(Routing.Main.toString()) {

            MainScreen(
                rememberViewModelFactory(viewModelAssistedFactories),
                onNavigateToSettings = { navController.navigate(Routing.Settings.toString()) }
            )
        }

        composable(Routing.Settings.toString()) {

            SettingsScreen(
                rememberViewModelFactory(viewModelAssistedFactories),
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}

@Composable
private fun rememberViewModelFactory(
    viewModelAssistedFactories: ViewModelAssistedFactoryMap,
): AppSavedStateViewModelFactory {

    val savedStateRegistryOwner = LifecycleOwnerAmbient.current as SavedStateRegistryOwner

    return remember {
        AppSavedStateViewModelFactory(savedStateRegistryOwner, viewModelAssistedFactories)
    }
}
