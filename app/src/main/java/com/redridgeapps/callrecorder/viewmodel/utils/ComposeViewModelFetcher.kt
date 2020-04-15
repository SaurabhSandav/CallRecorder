package com.redridgeapps.callrecorder.viewmodel.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.redridgeapps.callrecorder.viewmodel.ComposeViewModelStores
import com.redridgeapps.repository.viewmodel.ViewModelMarker
import com.redridgeapps.repository.viewmodel.utils.IViewModelFetcher
import javax.inject.Inject
import javax.inject.Provider
import kotlin.reflect.KClass

class ComposeViewModelFetcher(
    private val composeViewModelStores: ComposeViewModelStores,
    private val viewModelFactory: DaggerViewModelFactory,
    private val creators: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : IViewModelFetcher {

    override fun <T : ViewModelMarker> fetch(key: String, kClass: KClass<T>): T {

        val viewModelStore = composeViewModelStores.getViewModelStore(key)
        val viewModelProvider = ViewModelProvider(viewModelStore, viewModelFactory)

        val clazz: Class<out ViewModel> = creators.keys.firstOrNull {
            kClass.java.isAssignableFrom(it)
        } ?: error("Invalid class: ${kClass.qualifiedName}")

        @Suppress("UNCHECKED_CAST")
        return viewModelProvider.get(clazz) as T
    }
}

class ComposeViewModelFetcherFactory @Inject constructor(
    private val viewModelFactory: DaggerViewModelFactory,
    private val creators: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) {
    fun create(composeViewModelStores: ComposeViewModelStores): ComposeViewModelFetcher {
        return ComposeViewModelFetcher(composeViewModelStores, viewModelFactory, creators)
    }
}
