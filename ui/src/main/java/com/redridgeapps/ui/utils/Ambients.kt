package com.redridgeapps.ui.utils

import androidx.compose.Composable
import androidx.compose.ProvidedValue
import androidx.compose.Providers
import androidx.compose.staticAmbientOf
import com.redridgeapps.repository.viewmodel.utils.IComposeViewModelStores
import com.redridgeapps.repository.viewmodel.utils.IViewModelFetcher
import com.redridgeapps.ui.initialization.UIInitializer
import javax.inject.Provider

val UIInitializersAmbient =
    staticAmbientOf<Map<Class<out UIInitializer>, Provider<UIInitializer>>>()

val ComposeViewModelStoresAmbient = staticAmbientOf<IComposeViewModelStores>()

val ViewModelFetcherAmbient = staticAmbientOf<IViewModelFetcher>()

@Composable
fun WithAmbients(vararg values: ProvidedValue<*>, content: @Composable() () -> Unit) {
    Providers(*values, children = content)
}
