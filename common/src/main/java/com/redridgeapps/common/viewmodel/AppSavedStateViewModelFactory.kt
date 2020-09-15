package com.redridgeapps.common.viewmodel

import android.annotation.SuppressLint
import androidx.hilt.lifecycle.ViewModelAssistedFactory
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import javax.inject.Provider

typealias ViewModelAssistedFactoryMap = Map<String, @JvmSuppressWildcards Provider<ViewModelAssistedFactory<out ViewModel>>>

class AppSavedStateViewModelFactory(
    owner: SavedStateRegistryOwner,
    private val viewModelAssistedFactoryMap: ViewModelAssistedFactoryMap,
) : AbstractSavedStateViewModelFactory(owner, null) {

    @Suppress("UNCHECKED_CAST")
    @SuppressLint("RestrictedApi")
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle,
    ): T {

        val viewModelAssistedFactory = viewModelAssistedFactoryMap[modelClass.name]?.get()
            ?: error("ViewModelAssistedFactory (${modelClass.name}) not found")

        return viewModelAssistedFactory.create(handle) as T
    }
}
