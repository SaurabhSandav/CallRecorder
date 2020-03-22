package com.redridgeapps.ui.utils

import androidx.activity.result.ActivityResultRegistry
import androidx.compose.staticAmbientOf
import androidx.lifecycle.Lifecycle
import com.redridgeapps.repository.viewmodel.utils.IComposeViewModelStores
import com.redridgeapps.repository.viewmodel.utils.IViewModelFetcher

val LifecycleAmbient = staticAmbientOf<Lifecycle>()

val ActivityResultRegistryAmbient = staticAmbientOf<ActivityResultRegistry>()

val ComposeViewModelStoresAmbient = staticAmbientOf<IComposeViewModelStores>()

val ViewModelFetcherAmbient = staticAmbientOf<IViewModelFetcher>()
