package com.redridgeapps.compose.viewmodel

import android.annotation.SuppressLint
import androidx.hilt.lifecycle.ViewModelAssistedFactory
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

internal class ComposeSavedStateViewModelFactory(
    owner: ComposeOwner,
    private val viewModelAssistedFactory: ViewModelAssistedFactory<out ViewModel>
) : AbstractSavedStateViewModelFactory(owner, null) {

    @Suppress("UNCHECKED_CAST")
    @SuppressLint("RestrictedApi")
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T = viewModelAssistedFactory.create(handle) as T
}
