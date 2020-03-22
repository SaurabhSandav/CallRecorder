package com.redridgeapps.ui

import android.Manifest
import androidx.compose.Composable
import androidx.compose.Model
import androidx.compose.onActive
import androidx.ui.animation.Crossfade
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
    var isAppSystemized: Boolean? = null,
    var permissionsGranted: Boolean? = null,
    var captureAudioOutputPermissionGranted: Boolean? = null
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
        if (isAppSystemized == true && permissionsGranted == true && captureAudioOutputPermissionGranted == true)
            configurationFinished(viewModel, BackStackAmbient.current)
    }

    val topAppBar = @Composable {
        TopAppBar(
            title = { Text("Configure App", LayoutPadding(bottom = 16.dp)) }
        )
    }

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

private fun configurationFinished(viewModel: IFirstRunViewModel, backStack: BackStack<Any>) {
    viewModel.configurationFinished()
    backStack.push(MainDestination)
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

        Crossfade(current = viewModel.firstRunState.isAppSystemized) { isAppSystemized ->
            when (isAppSystemized) {
                null -> CircularProgressIndicator()
                true -> Text(text = "✔ App is systemized")
                false -> Button(onClick = { viewModel.systemize() }) { Text(text = "Systemize App") }
            }
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

        onActive {
            viewModel.firstRunState.permissionsGranted =
                permissionsManager.getUnGrantedPermissions().isEmpty()
        }

        Crossfade(
            current = viewModel.firstRunState.permissionsGranted
        ) { permissionsGranted ->

            permissionsGranted ?: return@Crossfade

            if (permissionsGranted) {
                Text("✔ All permissions granted")
            } else {

                val onClick = {
                    permissionsManager.requestPermissions { result ->
                        viewModel.firstRunState.permissionsGranted = result.denied.isEmpty()
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

        Text(
            text = "CAPTURE_AUDIO_OUTPUT is a special permission to enable high quality audio capture. " +
                    "It's granted automatically to system apps on boot. " +
                    "Please restart your device to finish configuration.",
            style = MaterialTheme.typography().subtitle1
        )

        Spacer(modifier = LayoutHeight(10.dp))

        val permissionsManager = PermissionsManager()

        onActive {
            viewModel.firstRunState.captureAudioOutputPermissionGranted =
                permissionsManager.checkPermissionGranted(Manifest.permission.CAPTURE_AUDIO_OUTPUT)
        }

        Crossfade(
            current = viewModel.firstRunState.captureAudioOutputPermissionGranted
        ) { permissionGranted ->

            permissionGranted ?: return@Crossfade

            if (permissionGranted) {
                Text(text = "✔ Permission granted", style = MaterialTheme.typography().subtitle1)
            } else {
                Text(
                    text = "✘ Permission not granted. Please restart device after systemization.",
                    style = MaterialTheme.typography().subtitle1.copy(Color.Red)
                )
            }
        }
    }
}
