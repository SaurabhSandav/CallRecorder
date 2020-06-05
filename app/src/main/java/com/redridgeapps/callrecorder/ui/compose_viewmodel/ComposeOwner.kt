package com.redridgeapps.callrecorder.ui.compose_viewmodel

import android.os.Bundle
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner

class ComposeOwner(
    private val savedStateKey: String,
    private val _viewModelStore: ViewModelStore
) : LifecycleOwner, SavedStateRegistryOwner, ViewModelStoreOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override fun getLifecycle(): Lifecycle = lifecycleRegistry

    override fun getSavedStateRegistry(): SavedStateRegistry {
        return savedStateRegistryController.savedStateRegistry
    }

    override fun getViewModelStore(): ViewModelStore = _viewModelStore

    fun performSave(outState: Bundle) {
        Bundle().let {
            savedStateRegistryController.performSave(it)
            outState.putBundle(savedStateKey, it)
        }
    }

    fun performRestore(savedState: Bundle?) {

        val bundle = savedState?.getBundle(savedStateKey)
        savedStateRegistryController.performRestore(bundle)

        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
    }
}
