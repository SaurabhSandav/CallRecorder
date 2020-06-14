package com.redridgeapps.compose.viewmodel

import androidx.hilt.lifecycle.ViewModelAssistedFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Provider
import kotlin.reflect.KClass

internal class ComposeViewModelFetcher(
    private val composeFramework: ComposeFramework,
    private val viewModelAssistedFactories: Map<String, @JvmSuppressWildcards Provider<ViewModelAssistedFactory<out ViewModel>>>
) {

    fun <T : ViewModel> fetch(key: String, kClass: KClass<T>): T {

        val viewModelAssistedFactory = viewModelAssistedFactories[kClass.qualifiedName]?.get()
            ?: error("ViewModelAssistedFactory not found")

        val owner = composeFramework.getComposeOwner(key)
        val viewModelFactory = ComposeSavedStateViewModelFactory(owner, viewModelAssistedFactory)
        val viewModelProvider = ViewModelProvider(owner.viewModelStore, viewModelFactory)

        return viewModelProvider.get(kClass.java)
    }
}
