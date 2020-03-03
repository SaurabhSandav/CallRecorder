package com.redridgeapps.ui.router

import com.redridgeapps.ui.utils.UIInitializer

interface Route {

    val uiInitializer: Class<out UIInitializer>
}
