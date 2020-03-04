package com.redridgeapps.repository.viewmodel.utils

import com.redridgeapps.repository.viewmodel.ViewModelMarker
import kotlin.reflect.KClass

interface IViewModelFetcher {

    fun <T : ViewModelMarker> fetch(
        key: String,
        kClass: KClass<T>
    ): T
}
