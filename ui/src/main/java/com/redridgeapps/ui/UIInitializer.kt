package com.redridgeapps.ui

import androidx.compose.Composable
import kotlin.reflect.KClass

interface UIInitializer {

    @Composable
    fun initialize()

    companion object {
        @Composable
        fun get(value: KClass<out UIInitializer>): UIInitializer {
            val uiInitializers = UIInitializersAmbient.current
            return uiInitializers[value.java]?.get() ?: error("UIInitializer not found")
        }
    }
}
