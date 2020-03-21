package com.redridgeapps.ui

import android.Manifest
import androidx.compose.Composable
import androidx.compose.Model
import androidx.ui.core.Text
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.material.*
import androidx.ui.unit.dp
import com.koduok.compose.navigation.BackStackAmbient
import com.koduok.compose.navigation.core.BackStack
import com.redridgeapps.repository.viewmodel.IFirstRunViewModel
import com.redridgeapps.ui.routing.Destination
import com.redridgeapps.ui.utils.PermissionsManager
import com.redridgeapps.ui.utils.fetchViewModel

@Model
class FirstRunState(
    var isRefreshing: Boolean = false,
    var isAppSystemized: Boolean = false,
    var permissionsGranted: Boolean = false,
    var captureAudioOutputPermissionGranted: Boolean = false
)

private val IFirstRunViewModel.firstRunState: FirstRunState
    get() = uiState as FirstRunState

object FirstRunDestination : Destination {

    @Composable
    override fun initializeUI() {

        val viewModel = fetchViewModel<IFirstRunViewModel>()

        FirstRunUI(viewModel)
    }
}

@Composable
private fun FirstRunUI(viewModel: IFirstRunViewModel) {

    with(viewModel.firstRunState) {
        if (isAppSystemized && permissionsGranted && captureAudioOutputPermissionGranted)
            configurationFinished(viewModel, BackStackAmbient.current)
    }

    val topAppBar = @Composable { TopAppBar(title = { Text("Configure App") }) }

    Scaffold(topAppBar = topAppBar) { modifier ->

        Column(LayoutSize.Fill + LayoutPadding(20.dp) + LayoutAlign.Center + modifier) {
            SystemizationConfig(viewModel)

            Spacer(modifier = LayoutHeight(20.dp))

            PermissionsConfig(viewModel)

            Spacer(modifier = LayoutHeight(20.dp))

            CaptureAudioConfig(viewModel)
        }
    }
}

@Composable
private fun SystemizationConfig(viewModel: IFirstRunViewModel) {
    Column {

        Text(text = "Systemization", style = MaterialTheme.typography().h6)

        Spacer(modifier = LayoutHeight(10.dp))

        Text(
            text = "App needs to be a system app. High quality call recording only works with system apps.",
            style = MaterialTheme.typography().subtitle1
        )

        Spacer(modifier = LayoutHeight(10.dp))

        when {
            viewModel.firstRunState.isRefreshing -> CircularProgressIndicator()
            viewModel.firstRunState.isAppSystemized -> Text(text = "✔ App is systemized")
            else -> Button(onClick = { viewModel.systemize() }) { Text(text = "Systemize App") }
        }
    }
}

@Composable
private fun PermissionsConfig(viewModel: IFirstRunViewModel) {
    Column {

        Text(text = "Permissions", style = MaterialTheme.typography().h6)

        Spacer(modifier = LayoutHeight(10.dp))

        val explanationText = """
            |App needs the following permissions:
            |
            | - RECORD_AUDIO
            | - READ_PHONE_STATE
            | - READ_CALL_LOG
            | - READ_CONTACTS
        """.trimMargin()

        Text(text = explanationText, style = MaterialTheme.typography().subtitle1)

        Spacer(modifier = LayoutHeight(10.dp))

        val permissionsManager = PermissionsManager()

        when {
            permissionsManager.getUnGrantedPermissions().isEmpty() -> {
                viewModel.firstRunState.permissionsGranted = true
                Text("✔ All permissions granted")
            }
            viewModel.firstRunState.isAppSystemized -> {

                val onClick = {
                    permissionsManager.requestPermissions {
                        viewModel.firstRunState.permissionsGranted = it.denied.isEmpty()
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
private fun CaptureAudioConfig(viewModel: IFirstRunViewModel) {
    Column {

        Text(text = "Permission CAPTURE_AUDIO_OUTPUT", style = MaterialTheme.typography().h6)

        Spacer(modifier = LayoutHeight(10.dp))

        val explanationText =
            "CAPTURE_AUDIO_OUTPUT is a special permission to enable high quality " +
                    "audio capture. It's granted automatically to system apps on boot. Please restart your device to finish configuration."

        Text(text = explanationText, style = MaterialTheme.typography().subtitle1)

        Spacer(modifier = LayoutHeight(10.dp))

        val permissionsManager = PermissionsManager()

        if (viewModel.firstRunState.isAppSystemized && viewModel.firstRunState.permissionsGranted) {

            viewModel.firstRunState.captureAudioOutputPermissionGranted =
                permissionsManager.checkPermissionGranted(Manifest.permission.CAPTURE_AUDIO_OUTPUT)

            if (viewModel.firstRunState.captureAudioOutputPermissionGranted) {
                Text(text = "✔ Permission granted", style = MaterialTheme.typography().subtitle1)
            } else {
                Text(
                    text = "✘ Permission not granted. Please restart device.",
                    style = MaterialTheme.typography().subtitle1.copy(Color.Red)
                )
            }
        }
    }
}

private fun configurationFinished(viewModel: IFirstRunViewModel, backStack: BackStack<Any>) {
    viewModel.configurationFinished()
    backStack.push(MainDestination)
}
