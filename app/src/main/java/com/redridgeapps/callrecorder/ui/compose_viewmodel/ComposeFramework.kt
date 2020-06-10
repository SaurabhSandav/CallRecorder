package com.redridgeapps.callrecorder.ui.compose_viewmodel

import android.os.Bundle
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelAssistedFactory
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import javax.inject.Provider
import kotlin.collections.set

class ComposeFramework @ViewModelInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    viewModelAssistedFactories: Map<String, @JvmSuppressWildcards Provider<ViewModelAssistedFactory<out ViewModel>>>
) : ViewModel() {

    private var savedState: Bundle? = null
    private val composeOwnerMap = mutableMapOf<String, ComposeOwner?>()
    private val viewModelStoreMap = mutableMapOf<String, ViewModelStore>()

    val viewModelFetcher = ComposeViewModelFetcher(this, viewModelAssistedFactories)

    init {
        setupSavedState()
    }

    fun initializeViewModel(key: String) {

        val composeOwner = composeOwnerMap[key]

        if (composeOwner == null) {

            val viewmodelStore = ViewModelStore()
            viewModelStoreMap[key] = viewmodelStore

            composeOwnerMap[key] = ComposeOwner(key, viewmodelStore).apply {
                performRestore(savedState)
            }
        }
    }

    fun destroyViewModel(key: String) {
        composeOwnerMap.remove(key)
        viewModelStoreMap.remove(key)?.clear()
    }

    fun getComposeOwner(key: String): ComposeOwner {
        return composeOwnerMap[key] ?: error("ComposeOwner does not exist")
    }

    private fun setupSavedState() {

        // Restore state
        val savedState = savedStateHandle.get<Bundle>(COMPOSE_FRAMEWORK_SAVED_STATE_KEY)
        restoreSavedState(savedState)

        // Save State
        savedStateHandle.setSavedStateProvider(COMPOSE_FRAMEWORK_SAVED_STATE_KEY) {
            Bundle().also { saveState(it) }
        }
    }

    private fun restoreSavedState(savedState: Bundle?) {

        this.savedState = savedState

        composeOwnerMap.replaceAll { key, _ ->
            ComposeOwner(key, viewModelStoreMap[key]!!).apply {
                performRestore(savedState)
            }
        }
    }

    private fun saveState(outState: Bundle) {
        composeOwnerMap.values.forEach { it?.performSave(outState) }
    }
}

const val COMPOSE_FRAMEWORK_SAVED_STATE_KEY = "COMPOSE_FRAMEWORK_SAVED_STATE_KEY"
