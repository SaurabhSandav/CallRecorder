package com.redridgeapps.ui.utils

import androidx.compose.*
import com.github.zsoltk.compose.router.BackStack
import com.redridgeapps.ui.initialization.Route
import com.redridgeapps.ui.initialization.UIInitializer
import javax.inject.Provider

val UIInitializersAmbient =
    staticAmbientOf<Map<Class<out UIInitializer>, Provider<UIInitializer>>>()

val BackStackAmbient = ambientOf<BackStack<Route>>()

@Composable
fun WithAmbients(vararg values: ProvidedValue<*>, content: @Composable() () -> Unit) {
    Providers(*values, children = content)
}
