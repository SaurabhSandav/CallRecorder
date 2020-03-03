package com.redridgeapps.ui.router

import androidx.compose.Model
import java.util.*

@Model
class BackStack internal constructor(initialItem: Route) {

    var top: Route = initialItem

    private val stack = ArrayDeque<Route>().apply { push(initialItem) }

    fun push(route: Route) {
        stack.push(route)
        updateTop()
    }

    fun pop(): Boolean {
        if (stack.size == 1) return false

        stack.pop()
        updateTop()

        return true
    }

    private fun updateTop() {
        top = stack.peek()!!
    }
}
