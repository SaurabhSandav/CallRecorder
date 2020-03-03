package com.redridgeapps.ui.router

class BackPressHandler {

    private var onBackPressHandler: (() -> Boolean)? = null

    fun handle(): Boolean = onBackPressHandler?.invoke() ?: false

    fun addOnBackPressHandler(block: () -> Boolean) {
        onBackPressHandler = block
    }
}
