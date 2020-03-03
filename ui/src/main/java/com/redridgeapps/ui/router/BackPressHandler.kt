package com.redridgeapps.ui.router

class BackPressHandler {

    var backStack: BackStack? = null

    fun handle(): Boolean = backStack?.pop() ?: false
}
