package com.redridgeapps.callrecorder

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.redridgeapps.callrecorder.screen.firstrun.FirstRunScreen
import com.redridgeapps.callrecorder.screen.main.MainScreen
import com.redridgeapps.callrecorder.screen.settings.SettingsScreen
import com.redridgeapps.callrecorder.theme.CallRecorderTheme

fun ComponentActivity.setupCompose(
    isFirstRun: Boolean,
) {

    setContent {
        CallRecorderTheme {
            Root(isFirstRun)
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
) {

    val start = when {
        isFirstRun -> Routing.FirstRun
        else -> Routing.Main
    }

    val navController = rememberNavController()

    NavHost(navController, startDestination = start.toString()) {

        composable(Routing.FirstRun.toString()) {

            FirstRunScreen(
                onConfigFinished = {
                    navController.popBackStack()
                    navController.navigate(Routing.Main.toString())
                }
            )
        }

        composable(Routing.Main.toString()) {

            MainScreen(
                onNavigateToSettings = { navController.navigate(Routing.Settings.toString()) }
            )
        }

        composable(Routing.Settings.toString()) {

            SettingsScreen(
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}
