package com.redridgeapps.callrecorder.ui.utils

import androidx.activity.result.ActivityResultRegistry
import androidx.compose.staticAmbientOf
import com.redridgeapps.callrecorder.ui.compose_viewmodel.ComposeViewModelFetcher
import com.redridgeapps.callrecorder.ui.compose_viewmodel.ComposeViewModelStores

val ActivityResultRegistryAmbient = staticAmbientOf<ActivityResultRegistry>()

val ComposeViewModelStoresAmbient = staticAmbientOf<ComposeViewModelStores>()

val ViewModelFetcherAmbient = staticAmbientOf<ComposeViewModelFetcher>()
