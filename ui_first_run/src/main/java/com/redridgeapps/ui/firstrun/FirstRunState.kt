package com.redridgeapps.ui.firstrun

import androidx.compose.getValue
import androidx.compose.mutableStateOf
import androidx.compose.setValue
import kotlinx.coroutines.flow.Flow

class FirstRunState(val isAppSystemized: Flow<Boolean>) {

    var permissionsGranted by mutableStateOf(false)

    var captureAudioOutputPermissionGranted by mutableStateOf(false)
}
