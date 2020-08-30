package com.redridgeapps.ui.common.routing

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import com.koduok.compose.navigation.BackStackAmbient
import com.koduok.compose.navigation.core.Route
import com.koduok.compose.navigation.core.backStackController
import com.redridgeapps.compose.viewmodel.ComposeFramework
import com.redridgeapps.compose.viewmodel.fetchViewModel
import kotlin.reflect.KClass

@Composable
inline fun <reified T : ViewModel> viewModel(): T = viewModel(T::class)

@Composable
fun <T : ViewModel> viewModel(kClass: KClass<T>): T {
    return fetchViewModel(kClass, BackStackAmbient.current.current.viewModelKey)
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
