package com.redridgeapps.callrecorder.ui.firstrun

import androidx.compose.getValue
import androidx.compose.mutableStateOf
import androidx.compose.setValue
import kotlinx.coroutines.flow.Flow

class FirstRunState(var isAppSystemized: Flow<Boolean>) {

    // var permissionsGranted: Boolean? by mutableStateOf(null)
    // Above syntax is clearer but does not work currently.
    // TODO Try above syntax again after new inference is enabled in compose
    var permissionsGranted by mutableStateOf<Boolean?>(null)

    var captureAudioOutputPermissionGranted by mutableStateOf<Boolean?>(null)
}