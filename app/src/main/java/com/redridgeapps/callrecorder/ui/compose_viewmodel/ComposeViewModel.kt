package com.redridgeapps.callrecorder.ui.compose_viewmodel

import androidx.compose.Composable
import androidx.lifecycle.ViewModel
import com.koduok.compose.navigation.BackStackAmbient
import com.koduok.compose.navigation.core.Route
import com.redridgeapps.callrecorder.ui.utils.ViewModelFetcherAmbient

@Composable
inline fun <reified T : ViewModel> fetchViewModel(): T {
    val viewModelFetcher = ViewModelFetcherAmbient.current
    val key = BackStackAmbient.current.current.viewModelStoreKey
    return viewModelFetcher.fetch(key, T::class)
}

val Route<*>.viewModelStoreKey: String
    get() = "${key}_$index"
