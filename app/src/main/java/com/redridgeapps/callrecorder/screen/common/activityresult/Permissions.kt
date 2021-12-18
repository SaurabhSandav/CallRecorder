package com.redridgeapps.callrecorder.screen.common.activityresult

import android.content.Context
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ContextAmbient
import com.redridgeapps.common.PermissionChecker

@Composable
fun rememberPermissionsRequest(
    vararg requestedPermissions: String,
    key: String = currentComposer.currentCompoundKeyHash.toString(),
    onResult: (permissionsResult: Map<String, Boolean>) -> Unit,
): () -> Unit {

    val manager = rememberActivityResultManager(
        key = key,
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        callback = { permissionsResult ->
            if (permissionsResult.isNotEmpty())
                onResult(permissionsResult)
        }
    )
    val context = ContextAmbient.current

    return remember {
        @Suppress("UNCHECKED_CAST")
        { manager.requestPermissions(context, requestedPermissions as Array<String>, onResult) }
    }
}

private fun ActivityResultManager<Array<String>, Map<String, Boolean>>.requestPermissions(
    context: Context,
    permissions: Array<String>,
    onResult: (permissionsResult: Map<String, Boolean>) -> Unit,
) {

    val permissionChecker = PermissionChecker(context)

    val permissionsStatus = permissions.associate {
        it to permissionChecker.isPermissionGranted(it)
    }

    when {
        permissionsStatus.all { it.value } -> onResult(permissionsStatus)
        else -> launch(permissions)
    }
}
