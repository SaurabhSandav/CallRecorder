package com.redridgeapps.ui

import androidx.compose.Composable
import androidx.compose.Model
import androidx.ui.core.Modifier
import androidx.ui.core.Text
import androidx.ui.layout.Column
import androidx.ui.material.Scaffold
import androidx.ui.material.TopAppBar
import com.redridgeapps.repository.viewmodel.ISettingsViewModel
import com.redridgeapps.ui.routing.Destination
import com.redridgeapps.ui.utils.SwitchPreference
import com.redridgeapps.ui.utils.fetchViewModel

@Model
class SettingsState(
    var isSystemized: Boolean? = null,
    var isRecordingOn: Boolean? = null
)

object SettingsDestination : Destination {

    @Composable
    override fun initializeUI() {

        val viewModel = fetchViewModel<ISettingsViewModel>()

        SettingsUI(viewModel)
    }
}

private val ISettingsViewModel.settingsState: SettingsState
    get() = uiState as SettingsState

@Composable
private fun SettingsUI(viewModel: ISettingsViewModel) {

    val topAppBar = @Composable() {
        TopAppBar({ Text("Settings") })
    }

    Scaffold(topAppBar = topAppBar) { modifier ->
        ContentMain(viewModel, modifier)
    }
}

@Composable
private fun ContentMain(viewModel: ISettingsViewModel, modifier: Modifier) {

    Column(modifier) {

        SwitchPreference("Recording", viewModel.settingsState.isRecordingOn) {
            viewModel.flipRecording()
        }

        SwitchPreference("Systemize", viewModel.settingsState.isSystemized) {
            viewModel.flipSystemization()
        }
    }
}
