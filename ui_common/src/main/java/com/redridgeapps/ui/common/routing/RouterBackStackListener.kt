package com.redridgeapps.ui.common.routing

import com.koduok.compose.navigation.core.BackStackController
import com.koduok.compose.navigation.core.GlobalRoute
import com.koduok.compose.navigation.core.Route
import timber.log.Timber

class RouterBackStackListener(
    val onRouteAdded: (Route<*>) -> Unit,
    val onRouteRemoved: (Route<*>) -> Unit
) : BackStackController.Listener {

    private var oldSnapshot = listOf<Route<*>>()

    override fun onBackStackChanged(snapshot: List<GlobalRoute>) {

        val newSnapshot = snapshot.flatMap { it.snapshot }

        if (oldSnapshot == snapshot) return

        val removed = oldSnapshot subtract newSnapshot
        val added = newSnapshot subtract oldSnapshot

        removed.forEach { route ->
            onRouteRemoved(route)

            Timber.d("Route removed: $route")
        }

        added.forEach { route ->
            onRouteAdded(route)

            Timber.d("Route added: $route")
        }

        oldSnapshot = newSnapshot
    }
}
