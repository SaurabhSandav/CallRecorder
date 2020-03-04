package com.redridgeapps.callrecorder.viewmodel.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import com.redridgeapps.repository.viewmodel.utils.IComposeViewModelStores
import java.util.*

class ComposeViewModelStores : ViewModel(), IComposeViewModelStores {

    private val viewModelStores = HashMap<String, ViewModelStore>()

    override fun addViewModelStore(key: String) {

        val viewModelStore = viewModelStores[key]

        if (viewModelStore == null)
            viewModelStores[key] = ViewModelStore()
    }

    override fun removeViewModelStore(key: String) {
        val viewModelStore = viewModelStores[key]

        if (viewModelStore != null) {
            viewModelStore.clear()
            viewModelStores.remove(key)
        }
    }

    fun getViewModelStore(key: String): ViewModelStore {
        return viewModelStores[key] ?: error("ViewModelStore does not exist")
    }
}
