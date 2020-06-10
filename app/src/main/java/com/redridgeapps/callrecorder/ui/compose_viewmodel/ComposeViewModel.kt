package com.redridgeapps.callrecorder.ui.compose_viewmodel

import androidx.compose.Composable
import androidx.lifecycle.ViewModel
import com.koduok.compose.navigation.BackStackAmbient
import com.koduok.compose.navigation.core.Route
import com.koduok.compose.navigation.core.backStackController
import com.redridgeapps.callrecorder.ui.routing.RouterBackStackListener
import kotlin.reflect.KClass

@Composable
inline fun <reified T : ViewModel> fetchViewModel(): T = fetchViewModel(T::class)

@Composable
fun <T : ViewModel> fetchViewModel(kClass: KClass<T>): T {
    val viewModelFetcher = ViewModelFetcherAmbient.current
    val key = BackStackAmbient.current.current.viewModelKey
    return viewModelFetcher.fetch(key, kClass)
}

fun ComposeFramework.setupViewModel() {

    val backStackListener = RouterBackStackListener(
        onRouteAdded = { initializeViewModel(it.viewModelKey) },
        onRouteRemoved = { destroyViewModel(it.viewModelKey) }
    )

    backStackController.addListener(backStackListener)
}

private val Route<*>.viewModelKey: String
    get() = "${key}_$index"
