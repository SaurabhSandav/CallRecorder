package com.redridgeapps.callrecorder.ui.compose_viewmodel

import android.os.Bundle
import androidx.activity.ComponentActivity
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

    fun setupSavedState(
        componentActivity: ComponentActivity
    ) = with(componentActivity.savedStateRegistry) {

        // Restore state
        val savedState = consumeRestoredStateForKey(COMPOSE_SAVED_STATE_KEY)
        restoreSavedState(savedState)

        // Save State
        registerSavedStateProvider(COMPOSE_SAVED_STATE_KEY) {
            Bundle().also { saveState(it) }
        }
    }

    fun getComposeOwner(key: String): ComposeOwner {
        return composeOwnerMap[key] ?: error("ComposeOwner does not exist")
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

const val COMPOSE_SAVED_STATE_KEY = "COMPOSE_SAVED_STATE_KEY"
