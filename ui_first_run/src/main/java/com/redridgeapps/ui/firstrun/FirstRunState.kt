package com.redridgeapps.ui.firstrun

import android.Manifest
import androidx.compose.runtime.Stable

@Stable
internal data class FirstRunState(
    val isAppSystemized: Boolean = false,
    val onAppSystemize: OnAppSystemize,

    val allPermissionsGranted: Boolean = false,
    val onPermissionsResult: OnPermissionsResult,

    val captureAudioOutputPermissionGranted: Boolean = false,

    val isConfigFinished: Boolean = false,
)

internal val REQUESTED_DANGEROUS_PERMISSIONS = listOf(
    Manifest.permission.RECORD_AUDIO,
    Manifest.permission.READ_PHONE_STATE,
    Manifest.permission.READ_CALL_LOG,
    Manifest.permission.READ_CONTACTS,
)

internal typealias OnAppSystemize = () -> Unit
internal typealias OnPermissionsResult = (Boolean) -> Unit
