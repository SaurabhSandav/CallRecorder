package com.redridgeapps.callrecorder.ui.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.Composable
import androidx.compose.CompositionLifecycleObserver
import androidx.compose.onPreCommit
import androidx.compose.remember
import androidx.core.content.ContextCompat
import androidx.ui.core.ContextAmbient

@Composable
fun requestPermissions(
    vararg requestedPermissions: String,
    key: String,
    onResult: (permissionResult: Map<String, Boolean>) -> Unit
) {

    val context = ContextAmbient.current
    val activityResultRegistry = ActivityResultRegistryAmbient.current
    val manager = remember { PermissionManager(key, context, activityResultRegistry) }

    onPreCommit {
        manager.requestPermission(requestedPermissions, onResult)
    }
}

@Composable
fun isPermissionGranted(permission: String): Boolean {
    val context = ContextAmbient.current
    return PermissionManager.isPermissionGranted(context, permission)
}

private class PermissionManager(
    private val key: String,
    private val context: Context,
    private val activityResultRegistry: ActivityResultRegistry
) : CompositionLifecycleObserver {

    private val contract = ActivityResultContracts.RequestMultiplePermissions()
    private var permissionRequest: ActivityResultLauncher<Array<String>>? = null
    private var onResult: ((Map<String, Boolean>) -> Unit)? = null

    override fun onEnter() {
        permissionRequest = activityResultRegistry.register(key, contract) { permissionResult ->
            onResult?.let { it(permissionResult) }
        }
    }

    override fun onLeave() {
        permissionRequest?.unregister()
        permissionRequest = null
    }

    fun requestPermission(
        permissions: Array<out String>,
        onResult: (permissionResult: Map<String, Boolean>) -> Unit
    ) {

        val unGrantedPermissions =
            permissions.filterNot { isPermissionGranted(context, it) }.toTypedArray()

        this.onResult = onResult
        permissionRequest?.launch(unGrantedPermissions)
    }

    companion object {

        fun isPermissionGranted(context: Context, permission: String): Boolean {
            val permissionStatus = ContextCompat.checkSelfPermission(context, permission)
            return permissionStatus == PackageManager.PERMISSION_GRANTED
        }
    }
}