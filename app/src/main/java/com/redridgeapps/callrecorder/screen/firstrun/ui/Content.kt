package com.redridgeapps.callrecorder.screen.firstrun.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.redridgeapps.callrecorder.screen.firstrun.OnAppSystemize
import com.redridgeapps.callrecorder.screen.firstrun.OnPermissionsResult

@Composable
internal fun Content(
    isAppSystemized: Boolean,
    onAppSystemize: OnAppSystemize,

    allPermissionsGranted: Boolean,
    onPermissionsResult: OnPermissionsResult,

    captureAudioOutputPermissionGranted: Boolean,
) {

    Scaffold(topBar = { FirstRunTopAppBar() }) { innerPadding ->

        val columnModifiers = Modifier.fillMaxSize()
            .padding(innerPadding)
            .padding(20.dp)
            .wrapContentSize(Alignment.Center)

        Column(
            modifier = columnModifiers,
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {

            SystemizationConfig(
                isAppSystemized = isAppSystemized,
                onAppSystemize = onAppSystemize
            )

            PermissionsConfig(
                allPermissionsGranted = allPermissionsGranted,
                onPermissionsResult = onPermissionsResult
            )

            CaptureAudioConfig(
                captureAudioOutputPermissionGranted = captureAudioOutputPermissionGranted
            )
        }
    }
}

@Composable
private fun FirstRunTopAppBar() {
    TopAppBar(
        title = { Text("Configure App") }
    )
}
