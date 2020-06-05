package com.redridgeapps.callrecorder.ui.routing

import com.koduok.compose.navigation.core.BackStackController
import com.koduok.compose.navigation.core.GlobalRoute
import com.koduok.compose.navigation.core.Route
import timber.log.Timber

class RouterBackStackListener(
    val onAdded: (Route<*>) -> Unit,
    val onRemoved: (Route<*>) -> Unit
) : BackStackController.Listener {

    private var oldSnapshot = listOf<Route<*>>()

    override fun onBackStackChanged(snapshot: List<GlobalRoute>) {

        val newSnapshot = snapshot.flatMap { it.snapshot }

        if (oldSnapshot == snapshot) return

        val removed = oldSnapshot.minus(newSnapshot)
        val added = newSnapshot.minus(oldSnapshot)

        removed.forEach { route ->
            onRemoved(route)

            Timber.d("Route removed: $route")
        }

        added.forEach { route ->
            onAdded(route)

            Timber.d("Route added: $route")
        }

        oldSnapshot = newSnapshot
    }
}
