package com.redridgeapps.compose_viewmodel

import androidx.compose.Composable
import androidx.compose.Providers
import androidx.compose.staticAmbientOf

val ViewModelFetcherAmbient = staticAmbientOf<ComposeViewModelFetcher>()

@Composable
fun WithViewModels(
    composeFramework: ComposeFramework,
    children: @Composable() () -> Unit
) {
    Providers(
        ViewModelFetcherAmbient provides composeFramework.viewModelFetcher,
        children = children
    )
}
