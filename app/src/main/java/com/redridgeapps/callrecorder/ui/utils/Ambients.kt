package com.redridgeapps.callrecorder.ui.utils

import androidx.activity.result.ActivityResultRegistry
import androidx.compose.staticAmbientOf
import com.redridgeapps.repository.viewmodel.utils.IComposeViewModelStores
import com.redridgeapps.repository.viewmodel.utils.IViewModelFetcher

val ActivityResultRegistryAmbient = staticAmbientOf<ActivityResultRegistry>()

val ComposeViewModelStoresAmbient = staticAmbientOf<IComposeViewModelStores>()

val ViewModelFetcherAmbient = staticAmbientOf<IViewModelFetcher>()
