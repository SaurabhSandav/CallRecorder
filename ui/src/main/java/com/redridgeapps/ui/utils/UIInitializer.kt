package com.redridgeapps.ui.utils

import androidx.compose.Composable

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
