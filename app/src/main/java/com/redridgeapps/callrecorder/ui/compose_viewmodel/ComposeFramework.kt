package com.redridgeapps.callrecorder.ui.compose_viewmodel

import android.os.Bundle
import androidx.hilt.lifecycle.ViewModelAssistedFactory
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import javax.inject.Provider
import kotlin.collections.set

class ComposeFramework @ViewModelInject constructor(
    viewModelAssistedFactories: Map<String, @JvmSuppressWildcards Provider<ViewModelAssistedFactory<out ViewModel>>>
) : ViewModel() {

    private var savedState: Bundle? = null
    private val composeOwnerMap = mutableMapOf<String, ComposeOwner?>()
    private val viewModelStoreMap = mutableMapOf<String, ViewModelStore>()

    val viewModelFetcher = ComposeViewModelFetcher(this, viewModelAssistedFactories)

    fun destinationAdded(key: String) {

        val composeOwner = composeOwnerMap[key]

        if (composeOwner == null) {

            val viewmodelStore = ViewModelStore()
            viewModelStoreMap[key] = viewmodelStore

            composeOwnerMap[key] = ComposeOwner(key, viewmodelStore).apply {
                performRestore(savedState)
            }
        }
    }

    fun destinationRemoved(key: String) {
        composeOwnerMap.remove(key)
        viewModelStoreMap.remove(key)?.clear()
    }

    fun restoreSavedState(savedState: Bundle?) {

        this.savedState = savedState

        composeOwnerMap.replaceAll { key, _ ->
            ComposeOwner(key, viewModelStoreMap[key]!!).apply {
                performRestore(savedState)
            }
        }
    }

    fun saveState(outState: Bundle) {
        composeOwnerMap.entries.removeAll { (_, owner) ->
            owner?.performSave(outState)
            true
        }
    }

    fun getComposeOwner(key: String): ComposeOwner {
        return composeOwnerMap[key] ?: error("ComposeOwner does not exist")
    }
}
