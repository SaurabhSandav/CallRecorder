package com.redridgeapps.callrecorder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import timber.log.Timber
import java.util.*

class ComposeViewModelStores : ViewModel() {

    private val viewModelStores = HashMap<String, ViewModelStore>()

    fun addViewModelStore(key: String) {

        val viewModelStore = viewModelStores[key]

        if (viewModelStore == null) {
            viewModelStores[key] = ViewModelStore()
            Timber.d("ViewModelStore ($key) Added")
        } else {
            Timber.d("ViewModelStore ($key) already exists")
        }
    }

    fun removeViewModelStore(key: String) {
        val viewModelStore = viewModelStores[key]

        if (viewModelStore != null) {
            viewModelStore.clear()
            viewModelStores.remove(key)
            Timber.d("ViewModelStore ($key) Removed")
        } else {
            Timber.d("ViewModelStore ($key) does not exist")
        }
    }

    fun getViewModelStore(key: String): ViewModelStore {
        Timber.d("Trying to acquire ViewModelStore ($key)")
        return viewModelStores[key] ?: error("ViewModelStore does not exist")
    }
}
