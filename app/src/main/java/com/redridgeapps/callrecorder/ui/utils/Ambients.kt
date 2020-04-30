package com.redridgeapps.callrecorder.ui.utils

import androidx.activity.result.ActivityResultRegistry
import androidx.compose.staticAmbientOf
import com.redridgeapps.callrecorder.viewmodel.ComposeViewModelStores
import com.redridgeapps.callrecorder.viewmodel.utils.ComposeViewModelFetcher

val ActivityResultRegistryAmbient = staticAmbientOf<ActivityResultRegistry>()

val ComposeViewModelStoresAmbient = staticAmbientOf<ComposeViewModelStores>()

val ViewModelFetcherAmbient = staticAmbientOf<ComposeViewModelFetcher>()
