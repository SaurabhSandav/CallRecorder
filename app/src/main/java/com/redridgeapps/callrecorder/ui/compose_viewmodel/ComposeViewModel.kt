package com.redridgeapps.callrecorder.ui.compose_viewmodel

import androidx.compose.Composable
import androidx.lifecycle.ViewModel
import com.koduok.compose.navigation.BackStackAmbient
import com.koduok.compose.navigation.core.Route
import com.koduok.compose.navigation.core.backStackController
import com.redridgeapps.callrecorder.ui.routing.RouterBackStackListener

@Composable
inline fun <reified T : ViewModel> fetchViewModel(): T {
    val viewModelFetcher = ViewModelFetcherAmbient.current
    val key = BackStackAmbient.current.current.viewModelStoreKey
    return viewModelFetcher.fetch(key, T::class)
}

fun ComposeFramework.setupViewModel() {

    val backStackListener = RouterBackStackListener(
        onRouteAdded = { initializeViewModel(it.viewModelStoreKey) },
        onRouteRemoved = { destroyViewModel(it.viewModelStoreKey) }
    )

    backStackController.addListener(backStackListener)
}

val Route<*>.viewModelStoreKey: String
    get() = "${key}_$index"
