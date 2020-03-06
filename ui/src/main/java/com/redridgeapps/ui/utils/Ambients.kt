package com.redridgeapps.ui.utils

import androidx.compose.Composable
import androidx.compose.ProvidedValue
import androidx.compose.Providers
import androidx.compose.staticAmbientOf
import com.redridgeapps.repository.viewmodel.utils.IComposeViewModelStores
import com.redridgeapps.repository.viewmodel.utils.IViewModelFetcher

val ComposeViewModelStoresAmbient = staticAmbientOf<IComposeViewModelStores>()

val ViewModelFetcherAmbient = staticAmbientOf<IViewModelFetcher>()

@Composable
fun WithAmbients(vararg values: ProvidedValue<*>, content: @Composable() () -> Unit) {
    Providers(*values, children = content)
}
