package com.redridgeapps.callrecorder.ui.utils

import androidx.activity.result.ActivityResultRegistry
import androidx.compose.staticAmbientOf
import com.redridgeapps.callrecorder.ui.compose_viewmodel.ComposeViewModelFetcher

val ActivityResultRegistryAmbient = staticAmbientOf<ActivityResultRegistry>()

val ViewModelFetcherAmbient = staticAmbientOf<ComposeViewModelFetcher>()
