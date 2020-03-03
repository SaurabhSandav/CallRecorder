package com.redridgeapps.ui.initialization

interface Route {

    val uiInitializer: Class<out UIInitializer>
}
