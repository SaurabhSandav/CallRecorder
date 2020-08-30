package com.redridgeapps.ui.firstrun

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.Flow

internal class FirstRunState(val isAppSystemized: Flow<Boolean>) {

    var permissionsGranted by mutableStateOf(false)

    var captureAudioOutputPermissionGranted by mutableStateOf(false)
}
