package com.redridgeapps.ui

import android.app.Activity
import androidx.compose.Composable
import androidx.ui.core.setContent
import androidx.ui.material.MaterialTheme
import com.koduok.compose.navigation.Router
import com.redridgeapps.repository.viewmodel.utils.IComposeViewModelStores
import com.redridgeapps.repository.viewmodel.utils.IViewModelFetcher
import com.redridgeapps.ui.utils.ComposeViewModelStoresAmbient
import com.redridgeapps.ui.utils.ViewModelFetcherAmbient
import com.redridgeapps.ui.utils.WithAmbients
import com.redridgeapps.ui.utils.WithViewModelStores

fun Activity.showUI(
    isFirstRun: Boolean,
    composeViewModelStores: IComposeViewModelStores,
    composeViewModelFetcher: IViewModelFetcher
) {

    setContent {
        val content = @Composable() { Root(isFirstRun) }

        WithAmbients(
            ComposeViewModelStoresAmbient provides composeViewModelStores,
            ViewModelFetcherAmbient provides composeViewModelFetcher,
            content = content
        )
    }
}

// TODO Remove when disposition bug fixed
fun Activity.destroyUI() {
    setContent {}
}

@Composable
fun Root(isFirstRun: Boolean) {

    val destination = when {
        isFirstRun -> SystemizerDestination
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
