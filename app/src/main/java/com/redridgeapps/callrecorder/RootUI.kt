package com.redridgeapps.callrecorder

import androidx.activity.ComponentActivity
import androidx.compose.navigation.AmbientNavController
import androidx.compose.navigation.NavHost
import androidx.compose.navigation.composable
import androidx.compose.navigation.navigate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LifecycleOwnerAmbient
import androidx.compose.ui.platform.setContent
import androidx.savedstate.SavedStateRegistryOwner
import com.redridgeapps.callrecorder.theme.CallRecorderTheme
import com.redridgeapps.common.viewmodel.AppSavedStateViewModelFactory
import com.redridgeapps.common.viewmodel.ViewModelAssistedFactoryMap
import com.redridgeapps.ui.firstrun.FirstRunScreen
import com.redridgeapps.ui.main.MainScreen
import com.redridgeapps.ui.settings.SettingsScreen

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

    NavHost(startDestination = start) {

        composable(Routing.FirstRun) {

            val navController = AmbientNavController.current

            FirstRunScreen(
                rememberViewModelFactory(viewModelAssistedFactories),
                onConfigFinished = {
                    navController.popBackStack()
                    navController.navigate(Routing.Main)
                }
            )
        }

        composable(Routing.Main) {

            val navController = AmbientNavController.current

            MainScreen(
                rememberViewModelFactory(viewModelAssistedFactories),
                onNavigateToSettings = { navController.navigate(Routing.Settings) }
            )
        }

        composable(Routing.Settings) {

            val navController = AmbientNavController.current

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
