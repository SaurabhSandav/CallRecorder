package com.redridgeapps.callrecorder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import com.redridgeapps.repository.viewmodel.utils.IComposeViewModelStores
import timber.log.Timber
import java.util.*

class ComposeViewModelStores : ViewModel(), IComposeViewModelStores {

    private val viewModelStores = HashMap<String, ViewModelStore>()

    override fun addViewModelStore(key: String) {

        val viewModelStore = viewModelStores[key]

        if (viewModelStore == null) {
            viewModelStores[key] = ViewModelStore()
            Timber.d("ViewModelStore ($key) Added")
        } else {
            Timber.d("ViewModelStore ($key) already exists")
        }
    }

    override fun removeViewModelStore(key: String) {
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
