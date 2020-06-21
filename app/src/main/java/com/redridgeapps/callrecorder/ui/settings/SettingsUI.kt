package com.redridgeapps.callrecorder.ui.settings

import androidx.compose.Composable
import androidx.compose.collectAsState
import androidx.compose.getValue
import androidx.ui.core.Modifier
import androidx.ui.foundation.Icon
import androidx.ui.foundation.Text
import androidx.ui.layout.Column
import androidx.ui.layout.padding
import androidx.ui.material.IconButton
import androidx.ui.material.ListItem
import androidx.ui.material.Scaffold
import androidx.ui.material.TopAppBar
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.ArrowBack
import androidx.ui.unit.dp
import com.koduok.compose.navigation.BackStackAmbient
import com.redridgeapps.callrecorder.callutils.recording.PcmChannels
import com.redridgeapps.callrecorder.callutils.recording.PcmEncoding
import com.redridgeapps.callrecorder.callutils.recording.PcmSampleRate
import com.redridgeapps.callrecorder.ui.prefcomponents.SingleSelectListPreference
import com.redridgeapps.callrecorder.ui.prefcomponents.SwitchPreference
import com.redridgeapps.callrecorder.ui.prefcomponents.TextFieldPreference
import com.redridgeapps.callrecorder.ui.prefcomponents.TitlePreference
import com.redridgeapps.callrecorder.ui.routing.Destination
import com.redridgeapps.callrecorder.ui.routing.viewModel

object SettingsDestination : Destination {

    @Composable
    override fun initializeUI() {

        val viewModel = viewModel<SettingsViewModel>()

        SettingsUI(viewModel)
    }
}

@Composable
private fun SettingsUI(viewModel: SettingsViewModel) {

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

    Scaffold(topBar = topAppBar) { innerPadding ->
        ContentMain(viewModel, Modifier.padding(innerPadding))
    }
}

@Composable
private fun ContentMain(viewModel: SettingsViewModel, modifier: Modifier) {

    Column(modifier) {

        val isRecordingOn by viewModel.uiState.recordingEnabled.collectAsState(initial = null)

        SwitchPreference("Recording", isRecordingOn) {
            viewModel.flipRecordingEnabled()
        }

        val isSystemized by viewModel.uiState.isSystemized.collectAsState(initial = null)

        SwitchPreference("Systemize", isSystemized) {
            viewModel.flipSystemization()
        }

        ListItem(
            text = { Text("Update contact names") },
            onClick = { viewModel.updateContactNames() }
        )

        RecordingPreference(viewModel)
    }
}

@Composable
private fun RecordingPreference(viewModel: SettingsViewModel) {

    Column {

        TitlePreference(text = "Recording")

        val audioRecordSampleRate by viewModel.uiState.audioRecordSampleRate.collectAsState(initial = null)

        SingleSelectListPreference(
            title = "Sample Rate",
            keys = PcmSampleRate.values().asList(),
            itemText = { it.sampleRate.toString() },
            selectedItem = audioRecordSampleRate,
            onSelectedChange = { viewModel.setAudioRecordSampleRate(it) }
        )

        val audioRecordChannels by viewModel.uiState.audioRecordChannels.collectAsState(initial = null)

        SingleSelectListPreference(
            title = "Channels",
            keys = PcmChannels.values().asList(),
            itemText = { it.toReadableString() },
            selectedItem = audioRecordChannels,
            onSelectedChange = { viewModel.setAudioRecordChannels(it) }
        )

        val audioRecordEncoding by viewModel.uiState.audioRecordEncoding.collectAsState(initial = null)

        SingleSelectListPreference(
            title = "Encoding",
            keys = PcmEncoding.values().asList(),
            itemText = { it.toReadableString() },
            selectedItem = audioRecordEncoding,
            onSelectedChange = { viewModel.setAudioRecordEncoding(it) }
        )

        RecordingAutoDeletePreference(viewModel)
    }
}

@Composable
fun RecordingAutoDeletePreference(viewModel: SettingsViewModel) {

    val recordingAutoDeleteEnabled by viewModel.uiState.recordingAutoDeleteEnabled
        .collectAsState(initial = null)

    SwitchPreference("Auto delete", recordingAutoDeleteEnabled) {
        viewModel.flipRecordingAutoDeleteEnabled()
    }

    val autoDeleteAfterDays = viewModel.uiState.recordingAutoDeleteAfterDays
        .collectAsState(initial = null).value

    if (recordingAutoDeleteEnabled == true && autoDeleteAfterDays != null) {

        TextFieldPreference(
            title = "Auto delete after",
            text = autoDeleteAfterDays.toString(),
            onValueChange = { viewModel.setRecordingAutoDeleteAfterDays(it.toInt()) }
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
