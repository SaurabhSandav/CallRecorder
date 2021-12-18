package com.redridgeapps.callrecorder.screen.firstrun.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun CaptureAudioConfig(captureAudioOutputPermissionGranted: Boolean) {

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

        Text(text = "Permission CAPTURE_AUDIO_OUTPUT", style = MaterialTheme.typography.h6)

        Text(
            text = "CAPTURE_AUDIO_OUTPUT is a special permission to enable high quality audio capture. " +
                    "It's granted automatically to system apps on boot. " +
                    "Please restart your device to finish configuration.",
            style = MaterialTheme.typography.subtitle1
        )

        Crossfade(current = captureAudioOutputPermissionGranted) { permissionGranted ->

            if (permissionGranted) {
                Text(text = "✔ Permission granted", style = MaterialTheme.typography.subtitle1)
            } else {
                Text(
                    text = "✘ Permission not granted. Please restart device after systemization.",
                    style = MaterialTheme.typography.subtitle1.copy(Color.Red)
                )
            }
        }
    }
}
