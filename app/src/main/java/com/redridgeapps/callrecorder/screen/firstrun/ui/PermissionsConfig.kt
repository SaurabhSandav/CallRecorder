package com.redridgeapps.callrecorder.screen.firstrun.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.redridgeapps.callrecorder.screen.common.activityresult.rememberPermissionsRequest
import com.redridgeapps.callrecorder.screen.firstrun.OnPermissionsResult
import com.redridgeapps.callrecorder.screen.firstrun.REQUESTED_DANGEROUS_PERMISSIONS

@Composable
internal fun PermissionsConfig(
    allPermissionsGranted: Boolean,
    onPermissionsResult: OnPermissionsResult,
) {

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

        Text(text = "Permissions", style = MaterialTheme.typography.h6)

        Text(
            text = """
                |App needs the following permissions:
                |
                | - RECORD_AUDIO
                | - READ_PHONE_STATE
                | - READ_CALL_LOG
                | - READ_CONTACTS
                """.trimMargin(),
            style = MaterialTheme.typography.subtitle1,
        )

        val requestPermissions = rememberPermissionsRequest(
            requestedPermissions = REQUESTED_DANGEROUS_PERMISSIONS.toTypedArray(),
            onResult = { permissionResult ->
                onPermissionsResult(permissionResult.all { it.value })
            }
        )

        Crossfade(allPermissionsGranted) { permissionsGranted ->

            if (permissionsGranted) {
                Text("✔ All permissions granted")
            } else {
                Button(onClick = requestPermissions) {
                    Text(text = "Grant permissions")
                }
            }
        }
    }
}
