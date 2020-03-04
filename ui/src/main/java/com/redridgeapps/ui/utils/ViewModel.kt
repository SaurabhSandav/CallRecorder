package com.redridgeapps.ui.utils

import androidx.compose.Composable
import com.koduok.compose.navigation.BackStackAmbient
import com.koduok.compose.navigation.core.BackStackController
import com.koduok.compose.navigation.core.GlobalRoute
import com.koduok.compose.navigation.core.Route
import com.koduok.compose.navigation.core.backStackController
import com.redridgeapps.repository.viewmodel.ViewModelMarker
import com.redridgeapps.repository.viewmodel.utils.IComposeViewModelStores
import com.redridgeapps.repository.viewmodel.utils.IViewModelFetcher
import timber.log.Timber

@Composable
fun WithViewModelStores(block: @Composable() () -> Unit) {

    val composeViewModelStores = ComposeViewModelStoresAmbient.current
    val backStackListener = ViewModelStoreBackStackListener(composeViewModelStores)
    backStackController.addListener(backStackListener)

    block()
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
                composeViewModelStores.removeViewModelStore(removed.key.toString())

            if (added != null)
                composeViewModelStores.addViewModelStore(added.key.toString())

            oldSnapshot = newSnapshot

            Timber.d("Removed: $removed, Added: $added")
        }
    }

    private fun <T> List<T>.onlySingleOrNull(): T? = when {
        size > 1 -> error("Can only handle single element BackStack changes")
        else -> singleOrNull()
    }
}

@Composable
inline fun <reified T : ViewModelMarker> IViewModelFetcher.fetch(): T {
    val key = getViewModelKey()
    return fetch(key, T::class)
}

@Composable
fun getViewModelKey(): String {
    val backStack = BackStackAmbient.current
    val currentDestination = backStack.current
    return currentDestination.key.toString() + "_" + currentDestination.index
}
