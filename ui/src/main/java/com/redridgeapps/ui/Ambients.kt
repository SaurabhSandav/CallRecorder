package com.redridgeapps.ui

import androidx.compose.Composable
import androidx.compose.ProvidedValue
import androidx.compose.Providers
import androidx.compose.staticAmbientOf
import javax.inject.Provider

val UIInitializersAmbient =
    staticAmbientOf<Map<Class<out UIInitializer>, Provider<UIInitializer>>>()

@Composable
fun WithAmbients(vararg values: ProvidedValue<*>, content: @Composable() () -> Unit) {
    Providers(*values, children = content)
}
