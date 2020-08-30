package com.redridgeapps.compose.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.staticAmbientOf
import androidx.lifecycle.ViewModel
import kotlin.reflect.KClass

private val ViewModelFetcherAmbient = staticAmbientOf<ComposeViewModelFetcher>()

@Composable
fun WithViewModels(
    composeFramework: ComposeFramework,
    children: @Composable () -> Unit
) {
    Providers(
        ViewModelFetcherAmbient provides composeFramework.viewModelFetcher,
        children = children
    )
}

@Composable
fun <T : ViewModel> fetchViewModel(kClass: KClass<T>, key: String): T {
    return ViewModelFetcherAmbient.current.fetch(key, kClass)
}
