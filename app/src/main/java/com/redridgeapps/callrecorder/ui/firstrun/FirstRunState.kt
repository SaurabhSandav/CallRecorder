package com.redridgeapps.callrecorder.ui.firstrun

import androidx.compose.getValue
import androidx.compose.mutableStateOf
import androidx.compose.setValue

class FirstRunState {

    // var isAppSystemized: Boolean? by mutableStateOf(null)
    // Above syntax is clearer but does not work currently.
    // TODO Try above syntax again after new inference is enabled in compose
    var isAppSystemized by mutableStateOf<Boolean?>(null)

    var permissionsGranted by mutableStateOf<Boolean?>(null)

    var captureAudioOutputPermissionGranted by mutableStateOf<Boolean?>(null)
}