package com.redridgeapps.callrecorder.ui.firstrun

import android.Manifest
import androidx.compose.Composable
import androidx.compose.Model
import androidx.compose.onActive
import androidx.ui.animation.Crossfade
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.graphics.Color
import androidx.ui.layout.Column
import androidx.ui.layout.Spacer
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.layout.wrapContentSize
import androidx.ui.material.Button
import androidx.ui.material.CircularProgressIndicator
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Scaffold
import androidx.ui.material.TopAppBar
import androidx.ui.unit.dp
import com.koduok.compose.navigation.BackStackAmbient
import com.koduok.compose.navigation.core.BackStack
import com.redridgeapps.callrecorder.ui.main.MainDestination
import com.redridgeapps.callrecorder.ui.routing.Destination
import com.redridgeapps.callrecorder.ui.utils.PermissionsManager
import com.redridgeapps.callrecorder.ui.utils.fetchViewModel

@Model
class FirstRunState(
    var isAppSystemized: Boolean? = null,
    var permissionsGranted: Boolean? = null,
    var captureAudioOutputPermissionGranted: Boolean? = null
)

object FirstRunDestination : Destination {

    @Composable
    override fun initializeUI() {

        val viewModel = fetchViewModel<FirstRunViewModel>()

        FirstRunUI(viewModel)
    }
}

@Composable
private fun FirstRunUI(viewModel: FirstRunViewModel) {

    with(viewModel.uiState) {
        if (isAppSystemized == true && permissionsGranted == true && captureAudioOutputPermissionGranted == true)
            configurationFinished(viewModel, BackStackAmbient.current)
    }

    val topAppBar = @Composable {
        TopAppBar(
            title = { Text("Configure App", Modifier.padding(bottom = 16.dp)) }
        )
    }

    Scaffold(topAppBar = topAppBar) { modifier ->

        Column(Modifier.fillMaxSize().padding(20.dp).wrapContentSize(Alignment.Center) + modifier) {
            SystemizationConfig(viewModel)

            Spacer(modifier = Modifier.preferredHeight(20.dp))

            PermissionsConfig(viewModel)

            Spacer(modifier = Modifier.preferredHeight(20.dp))

            CaptureAudioConfig(viewModel)
        }
    }
}

private fun configurationFinished(viewModel: FirstRunViewModel, backStack: BackStack<Any>) {
    viewModel.configurationFinished()
    backStack.push(MainDestination)
}

@Composable
private fun SystemizationConfig(viewModel: FirstRunViewModel) {
    Column {

        Text(text = "Systemization", style = MaterialTheme.typography.h6)

        Spacer(modifier = Modifier.preferredHeight(10.dp))

        Text(
            text = "App needs to be a system app. High quality call recording only works with system apps.",
            style = MaterialTheme.typography.subtitle1
        )

        Spacer(modifier = Modifier.preferredHeight(10.dp))

        Crossfade(current = viewModel.uiState.isAppSystemized) { isAppSystemized ->
            when (isAppSystemized) {
                null -> CircularProgressIndicator()
                true -> Text(text = "✔ App is systemized")
                false -> Button(onClick = { viewModel.systemize() }) { Text(text = "Systemize App") }
            }
        }
    }
}

@Composable
private fun PermissionsConfig(viewModel: FirstRunViewModel) {
    Column {

        Text(text = "Permissions", style = MaterialTheme.typography.h6)

        Spacer(modifier = Modifier.preferredHeight(10.dp))

        val explanationText = """
            |App needs the following permissions:
            |
            | - RECORD_AUDIO
            | - READ_PHONE_STATE
            | - READ_CALL_LOG
            | - READ_CONTACTS
        """.trimMargin()

        Text(text = explanationText, style = MaterialTheme.typography.subtitle1)

        Spacer(modifier = Modifier.preferredHeight(10.dp))

        val permissionsManager = PermissionsManager()

        onActive {
            viewModel.uiState.permissionsGranted =
                permissionsManager.getUnGrantedPermissions().isEmpty()
        }

        Crossfade(
            current = viewModel.uiState.permissionsGranted
        ) { permissionsGranted ->

            permissionsGranted ?: return@Crossfade

            if (permissionsGranted) {
                Text("✔ All permissions granted")
            } else {

                val onClick = {
                    permissionsManager.requestPermissions { result ->
                        viewModel.uiState.permissionsGranted = result.denied.isEmpty()
                    }
                }

                Button(onClick = onClick) {
                    Text(text = "Grant permissions")
                }
            }
        }
    }
}

@Composable
private fun CaptureAudioConfig(viewModel: FirstRunViewModel) {
    Column {

        Text(text = "Permission CAPTURE_AUDIO_OUTPUT", style = MaterialTheme.typography.h6)

        Spacer(modifier = Modifier.preferredHeight(10.dp))

        Text(
            text = "CAPTURE_AUDIO_OUTPUT is a special permission to enable high quality audio capture. " +
                    "It's granted automatically to system apps on boot. " +
                    "Please restart your device to finish configuration.",
            style = MaterialTheme.typography.subtitle1
        )

        Spacer(modifier = Modifier.preferredHeight(10.dp))

        val permissionsManager = PermissionsManager()

        onActive {
            viewModel.uiState.captureAudioOutputPermissionGranted =
                permissionsManager.checkPermissionGranted(Manifest.permission.CAPTURE_AUDIO_OUTPUT)
        }

        Crossfade(
            current = viewModel.uiState.captureAudioOutputPermissionGranted
        ) { permissionGranted ->

            permissionGranted ?: return@Crossfade

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
