package com.redridgeapps.callrecorder.ui.firstrun

import android.Manifest
import androidx.compose.Composable
import androidx.compose.collectAsState
import androidx.compose.getValue
import androidx.compose.invalidate
import androidx.ui.animation.Crossfade
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.material.*
import androidx.ui.unit.dp
import com.koduok.compose.navigation.BackStackAmbient
import com.redridgeapps.callrecorder.ui.main.MainDestination
import com.redridgeapps.callrecorder.ui.routing.Destination
import com.redridgeapps.callrecorder.ui.routing.fetchViewModel
import com.redridgeapps.callrecorder.ui.utils.isPermissionGranted
import com.redridgeapps.callrecorder.ui.utils.requestPermissions

object FirstRunDestination : Destination {

    @Composable
    override fun initializeUI() {

        val viewModel = fetchViewModel<FirstRunViewModel>()

        FirstRunUI(viewModel)
    }
}

@Composable
private fun FirstRunUI(viewModel: FirstRunViewModel) {

    // If configuration finished, navigate to MainDestination
    with(viewModel.uiState) {
        val isAppSystemized by isAppSystemized.collectAsState(false)
        val backStack = BackStackAmbient.current

        if (isAppSystemized && permissionsGranted && captureAudioOutputPermissionGranted) {
            viewModel.configurationFinished()
            backStack.push(MainDestination)
        }
    }

    val topBar = @Composable {
        TopAppBar(
            title = { Text("Configure App", Modifier.padding(bottom = 16.dp)) }
        )
    }

    Scaffold(topBar = topBar) { innerPadding ->

        Column(
            Modifier.fillMaxSize().padding(innerPadding).padding(20.dp)
                .wrapContentSize(Alignment.Center)
        ) {
            SystemizationConfig(viewModel)

            Spacer(modifier = Modifier.preferredHeight(20.dp))

            PermissionsConfig(viewModel)

            Spacer(modifier = Modifier.preferredHeight(20.dp))

            CaptureAudioConfig(viewModel)
        }
    }
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

        val isAppSystemized by viewModel.uiState.isAppSystemized.collectAsState(initial = null)

        Crossfade(current = isAppSystemized) {
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

        // Called to re-request permissions
        val recompose = invalidate

        requestPermissions(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_CONTACTS,
            key = "FIRST_RUN_CHECK_ALL_PERMISSIONS",
            onResult = { permissionResult ->
                viewModel.uiState.permissionsGranted = permissionResult.all { it.value }
            }
        )

        Crossfade(
            current = viewModel.uiState.permissionsGranted
        ) { permissionsGranted ->

            if (permissionsGranted) {
                Text("✔ All permissions granted")
            } else {
                Button(onClick = { recompose() }) {
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

        viewModel.uiState.captureAudioOutputPermissionGranted =
            isPermissionGranted(Manifest.permission.CAPTURE_AUDIO_OUTPUT)

        Crossfade(
            current = viewModel.uiState.captureAudioOutputPermissionGranted
        ) { permissionGranted ->

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
