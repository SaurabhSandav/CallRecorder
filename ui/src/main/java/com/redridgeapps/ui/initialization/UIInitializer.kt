package com.redridgeapps.ui.initialization

import androidx.compose.Composable
import com.redridgeapps.ui.utils.UIInitializersAmbient

interface UIInitializer {

    @Composable
    fun initialize()
}

@Composable
fun <T : UIInitializer> InitializeUI(clazz: Class<T>) {
    val uiInitializers = UIInitializersAmbient.current
    val initializer = uiInitializers[clazz]?.get() ?: error("UIInitializer not found")
    initializer.initialize()
}
