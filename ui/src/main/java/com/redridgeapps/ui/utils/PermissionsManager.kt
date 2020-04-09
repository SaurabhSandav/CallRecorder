package com.redridgeapps.ui.utils

import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PermissionInfo.PROTECTION_DANGEROUS
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.Composable
import androidx.compose.remember
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.ui.core.ContextAmbient
import androidx.ui.core.LifecycleOwnerAmbient

@Composable
fun PermissionsManager(): PermissionsManager {

    val context = ContextAmbient.current
    val lifecycleOwner = LifecycleOwnerAmbient.current
    val activityResultRegistry = ActivityResultRegistryAmbient.current

    return remember { PermissionsManager(context, lifecycleOwner, activityResultRegistry) }
}

class PermissionsManager(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val activityResultRegistry: ActivityResultRegistry
) {

    /**
     * Requests all un-granted permissions if [requestedPermissions] not supplied
     */
    fun requestPermissions(
        vararg requestedPermissions: String,
        onResult: (Result) -> Unit
    ) {

        val unGrantedPermissions = requestedPermissions.ifEmpty {
            getUnGrantedPermissions()
        }

        if (unGrantedPermissions.isEmpty()) {
            onResult(Result(unGrantedPermissions.asList(), emptyList()))
            return
        }

        val key = unGrantedPermissions.contentToString()

        val permissionRequest = activityResultRegistry.register(
            key,
            { lifecycleOwner.lifecycle }, // FIXME Type Inference bug
            ActivityResultContracts.RequestPermissions()
        ) { permissionResult ->

            val permissionResultSplit = permissionResult.asIterable()
                .partition { it.value }

            val granted = permissionResultSplit.first.map { it.key }
            val denied = permissionResultSplit.second.map { it.key }

            onResult(Result(granted, denied))
        }

        permissionRequest.launch(unGrantedPermissions)
    }

    fun getUnGrantedPermissions(): Array<String> = with(context) {

        val permissions = packageManager
            .getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
            .requestedPermissions
            .filter {
                packageManager.getPermissionInfo(it, 0).protection == PROTECTION_DANGEROUS
            }

        return permissions
            .filter { !checkPermissionGranted(it) }
            .toTypedArray()
    }

    fun checkPermissionGranted(permission: String): Boolean {
        val permissionStatus = ContextCompat.checkSelfPermission(context, permission)
        return permissionStatus == PackageManager.PERMISSION_GRANTED
    }

    data class Result(val granted: List<String>, val denied: List<String>)
}
