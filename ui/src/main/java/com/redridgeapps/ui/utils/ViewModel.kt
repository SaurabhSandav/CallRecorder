package com.redridgeapps.ui.utils

import androidx.compose.Composable
import com.koduok.compose.navigation.BackStackAmbient
import com.koduok.compose.navigation.core.BackStackController
import com.koduok.compose.navigation.core.GlobalRoute
import com.koduok.compose.navigation.core.Route
import com.koduok.compose.navigation.core.backStackController
import com.redridgeapps.repository.viewmodel.ViewModelMarker
import com.redridgeapps.repository.viewmodel.utils.IComposeViewModelStores
import timber.log.Timber

@Composable
fun WithViewModelStores(block: @Composable() () -> Unit) {

    val composeViewModelStores = ComposeViewModelStoresAmbient.current
    val backStackListener = ViewModelStoreBackStackListener(composeViewModelStores)
    backStackController.addListener(backStackListener)

    block()
}

@Composable
inline fun <reified T : ViewModelMarker> fetchViewModel(): T {
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

fun createViewModelStoreKey(route: Route<*>): String {
    return route.key.toString() + "_" + route.index
}

class ViewModelStoreBackStackListener(
    private val composeViewModelStores: IComposeViewModelStores
) : BackStackController.Listener {

    var oldSnapshot = listOf<Route<*>>()

    override fun onBackStackChanged(snapshot: List<GlobalRoute>) {

        val newSnapshot = snapshot.flatMap { it.snapshot }

        if (oldSnapshot != snapshot) {
            val removed = oldSnapshot.minus(newSnapshot).onlySingleOrNull()
            val added = newSnapshot.minus(oldSnapshot).onlySingleOrNull()

            if (removed != null)
                composeViewModelStores.removeViewModelStore(createViewModelStoreKey(removed))

            if (added != null)
                composeViewModelStores.addViewModelStore(createViewModelStoreKey(added))

            oldSnapshot = newSnapshot

            Timber.d("Removed: $removed, Added: $added")
        }
    }

    private fun <T> List<T>.onlySingleOrNull(): T? = when {
        size > 1 -> error("Can only handle single element BackStack changes")
        else -> singleOrNull()
    }
}
