package com.redridgeapps.callrecorder.viewmodel.utils

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.redridgeapps.callrecorder.di.modules.android.PerActivity
import com.redridgeapps.repository.viewmodel.ViewModelMarker
import com.redridgeapps.repository.viewmodel.utils.IViewModelFetcher
import javax.inject.Inject
import javax.inject.Provider
import kotlin.reflect.KClass

@PerActivity
class ComposeViewModelFetcher @Inject constructor(
    activity: AppCompatActivity,
    private val viewModelFactory: DaggerViewModelFactory,
    private val creators: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : IViewModelFetcher {

    private val composeViewModel by activity.viewModels<ComposeViewModelStores>()

    override fun <T : ViewModelMarker> fetch(key: String, kClass: KClass<T>): T {

        val viewModelStore = composeViewModel.getViewModelStore(key)
        val viewModelProvider = ViewModelProvider(viewModelStore, viewModelFactory)

        val clazz: Class<out ViewModel> = creators.keys.firstOrNull {
            kClass.java.isAssignableFrom(it)
        } ?: error("Invalid class")

        @Suppress("UNCHECKED_CAST")
        return viewModelProvider.get(clazz) as T
    }
}
