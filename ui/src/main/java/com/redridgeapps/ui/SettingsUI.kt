package com.redridgeapps.ui

import androidx.compose.Composable
import androidx.compose.Model
import androidx.ui.core.Modifier
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.layout.padding
import androidx.ui.material.IconButton
import androidx.ui.material.Scaffold
import androidx.ui.material.TopAppBar
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.ArrowBack
import androidx.ui.unit.dp
import com.koduok.compose.navigation.BackStackAmbient
import com.redridgeapps.repository.callutils.PcmChannels
import com.redridgeapps.repository.callutils.PcmEncoding
import com.redridgeapps.repository.callutils.PcmSampleRate
import com.redridgeapps.repository.viewmodel.ISettingsViewModel
import com.redridgeapps.ui.routing.Destination
import com.redridgeapps.ui.utils.SingleSelectListPreference
import com.redridgeapps.ui.utils.SwitchPreference
import com.redridgeapps.ui.utils.TitlePreference
import com.redridgeapps.ui.utils.fetchViewModel

@Model
class SettingsState(
    var isSystemized: Boolean? = null,
    var isRecordingOn: Boolean? = null,
    var audioRecordSampleRate: PcmSampleRate? = null,
    var audioRecordChannels: PcmChannels? = null,
    var audioRecordEncoding: PcmEncoding? = null
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

    val navigationIcon = @Composable {

        val backStack = BackStackAmbient.current

        IconButton(onClick = { backStack.pop() }) {
            Icon(Icons.Default.ArrowBack)
        }
    }

    val topAppBar = @Composable {
        TopAppBar(
            title = { Text("Settings", modifier = Modifier.padding(bottom = 16.dp)) },
            navigationIcon = navigationIcon
        )
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

        AudioRecordAPIPreference(viewModel)
    }
}

@Composable
private fun AudioRecordAPIPreference(viewModel: ISettingsViewModel) {

    Column {

        TitlePreference(text = "AudioRecord API")

        SingleSelectListPreference(
            title = "Sample Rate",
            keys = PcmSampleRate.values().asList(),
            keyToTextMapper = { it.sampleRate.toString() },
            selectedItem = viewModel.settingsState.audioRecordSampleRate,
            onSelectedChange = { viewModel.setAudioRecordSampleRate(it) }
        )

        SingleSelectListPreference(
            title = "Channels",
            keys = PcmChannels.values().asList(),
            keyToTextMapper = { it.toReadableString() },
            selectedItem = viewModel.settingsState.audioRecordChannels,
            onSelectedChange = { viewModel.setAudioRecordChannels(it) }
        )

        SingleSelectListPreference(
            title = "Encoding",
            keys = PcmEncoding.values().asList(),
            keyToTextMapper = { it.toReadableString() },
            selectedItem = viewModel.settingsState.audioRecordEncoding,
            onSelectedChange = { viewModel.setAudioRecordEncoding(it) }
        )
    }
}

private fun PcmChannels.toReadableString(): String = when (this) {
    PcmChannels.MONO -> "Mono"
    PcmChannels.STEREO -> "Stereo"
}

private fun PcmEncoding.toReadableString(): String = when (this) {
    PcmEncoding.PCM_8BIT -> "PCM_8BIT"
    PcmEncoding.PCM_16BIT -> "PCM_16BIT"
    PcmEncoding.PCM_FLOAT -> "PCM_FLOAT"
}
