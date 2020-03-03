package com.redridgeapps.ui.utils

import androidx.compose.*
import com.redridgeapps.ui.router.BackPressHandler
import com.redridgeapps.ui.router.BackStack
import javax.inject.Provider

val UIInitializersAmbient =
    staticAmbientOf<Map<Class<out UIInitializer>, Provider<UIInitializer>>>()

val BackStackAmbient = ambientOf<BackStack>()

val BackPressHandlerAmbient = ambientOf<BackPressHandler>()

@Composable
fun WithAmbients(vararg values: ProvidedValue<*>, content: @Composable() () -> Unit) {
    Providers(*values, children = content)
}
