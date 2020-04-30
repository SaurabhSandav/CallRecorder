package com.redridgeapps.callrecorder.ui.compose_viewmodel

import androidx.compose.Composable
import androidx.compose.onDispose
import androidx.lifecycle.ViewModel
import com.koduok.compose.navigation.BackStackAmbient
import com.koduok.compose.navigation.core.BackStackController
import com.koduok.compose.navigation.core.GlobalRoute
import com.koduok.compose.navigation.core.Route
import com.koduok.compose.navigation.core.backStackController
import com.redridgeapps.callrecorder.ui.utils.ComposeViewModelStoresAmbient
import com.redridgeapps.callrecorder.ui.utils.ViewModelFetcherAmbient
import timber.log.Timber

@Composable
fun WithViewModelStores(block: @Composable() () -> Unit) {

    val composeViewModelStores = ComposeViewModelStoresAmbient.current
    val backStackListener = ViewModelStoreBackStackListener(composeViewModelStores)

    backStackController.addListener(backStackListener)
    onDispose { backStackController.removeListener(backStackListener) }

    block()
}

@Composable
inline fun <reified T : ViewModel> fetchViewModel(): T {
    val viewModelFetcher = ViewModelFetcherAmbient.current
    val key = getViewModelKey()
    return viewModelFetcher.fetch(key, T::class)
}

@Composable
fun getViewModelKey(): String {
    val backStack = BackStackAmbient.current
    val currentRoute = backStack.current
    return createViewModelStoreKey(currentRoute)
}

private fun createViewModelStoreKey(route: Route<*>): String {
    return route.key.toString() + "_" + route.index
}

private class ViewModelStoreBackStackListener(
    private val composeViewModelStores: ComposeViewModelStores
) : BackStackController.Listener {

    var oldSnapshot = listOf<Route<*>>()

    override fun onBackStackChanged(snapshot: List<GlobalRoute>) {

        val newSnapshot = snapshot.flatMap { it.snapshot }

        if (oldSnapshot == snapshot) return

        val removed = oldSnapshot.minus(newSnapshot)
        val added = newSnapshot.minus(oldSnapshot)

        removed.forEach {
            composeViewModelStores.removeViewModelStore(createViewModelStoreKey(it))
        }

        added.forEach {
            composeViewModelStores.addViewModelStore(createViewModelStoreKey(it))
        }

        oldSnapshot = newSnapshot

        Timber.d("Removed: $removed, Added: $added")
    }
}
