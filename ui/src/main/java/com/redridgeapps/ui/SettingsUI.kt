package com.redridgeapps.ui

import androidx.compose.Composable
import androidx.compose.Model
import androidx.ui.core.Modifier
import androidx.ui.core.Text
import androidx.ui.layout.Column
import androidx.ui.material.Scaffold
import androidx.ui.material.TopAppBar
import com.redridgeapps.repository.callutils.*
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
    var recordingAPI: RecordingAPI? = null,
    var mediaRecorderChannels: MediaRecorderChannels? = null,
    var mediaRecorderSampleRate: MediaRecorderSampleRate? = null,
    var audioRecordSampleRate: AudioRecordSampleRate? = null,
    var audioRecordChannels: AudioRecordChannels? = null,
    var audioRecordEncoding: AudioRecordEncoding? = null
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

        SingleSelectListPreference(
            title = "Recording API",
            items = RecordingAPI.values().asList(),
            keyToTextMapper = { it.toReadableString() },
            selectedItem = viewModel.settingsState.recordingAPI,
            onSelectedChange = { viewModel.setRecordingAPI(it) }
        )

        when (viewModel.settingsState.recordingAPI) {
            RecordingAPI.MEDIA_RECORDER -> MediaRecorderAPIPreference(viewModel)
            RecordingAPI.AUDIO_RECORD -> AudioRecordAPIPreference(viewModel)
        }
    }
}

@Composable
private fun MediaRecorderAPIPreference(viewModel: ISettingsViewModel) {

    Column {

        TitlePreference(text = "MediaRecorder API")

        SingleSelectListPreference(
            title = "Sample Rate",
            items = MediaRecorderSampleRate.values().asList(),
            keyToTextMapper = { it.toReadableString() },
            selectedItem = viewModel.settingsState.mediaRecorderSampleRate,
            onSelectedChange = { viewModel.setMediaRecorderSampleRate(it) }
        )

        SingleSelectListPreference(
            title = "Channels",
            items = MediaRecorderChannels.values().asList(),
            keyToTextMapper = { it.toReadableString() },
            selectedItem = viewModel.settingsState.mediaRecorderChannels,
            onSelectedChange = { viewModel.setMediaRecorderChannels(it) }
        )
    }
}

@Composable
private fun AudioRecordAPIPreference(viewModel: ISettingsViewModel) {

    Column {

        TitlePreference(text = "AudioRecord API")

        SingleSelectListPreference(
            title = "Sample Rate",
            items = AudioRecordSampleRate.values().asList(),
            keyToTextMapper = { it.toReadableString() },
            selectedItem = viewModel.settingsState.audioRecordSampleRate,
            onSelectedChange = { viewModel.setAudioRecordSampleRate(it) }
        )

        SingleSelectListPreference(
            title = "Channels",
            items = AudioRecordChannels.values().asList(),
            keyToTextMapper = { it.toReadableString() },
            selectedItem = viewModel.settingsState.audioRecordChannels,
            onSelectedChange = { viewModel.setAudioRecordChannels(it) }
        )

        SingleSelectListPreference(
            title = "Encoding",
            items = AudioRecordEncoding.values().asList(),
            keyToTextMapper = { it.toReadableString() },
            selectedItem = viewModel.settingsState.audioRecordEncoding,
            onSelectedChange = { viewModel.setAudioRecordEncoding(it) }
        )
    }
}

private fun RecordingAPI.toReadableString(): String = when (this) {
    RecordingAPI.MEDIA_RECORDER -> "MediaRecorder"
    RecordingAPI.AUDIO_RECORD -> "AudioRecord"
}

private fun MediaRecorderChannels.toReadableString(): String = when (this) {
    MediaRecorderChannels.MONO -> "Mono"
    MediaRecorderChannels.STEREO -> "Stereo"
}

private fun MediaRecorderSampleRate.toReadableString(): String = when (this) {
    MediaRecorderSampleRate.S44_100 -> "44_100"
    MediaRecorderSampleRate.S48_000 -> "48_000"
}

private fun AudioRecordSampleRate.toReadableString(): String = when (this) {
    AudioRecordSampleRate.S44_100 -> "44_100"
    AudioRecordSampleRate.S48_000 -> "48_000"
}

private fun AudioRecordChannels.toReadableString(): String = when (this) {
    AudioRecordChannels.MONO -> "Mono"
    AudioRecordChannels.STEREO -> "Stereo"
}

private fun AudioRecordEncoding.toReadableString(): String = when (this) {
    AudioRecordEncoding.ENCODING_PCM_8BIT -> "PCM_8BIT"
    AudioRecordEncoding.ENCODING_PCM_16BIT -> "PCM_16BIT"
    AudioRecordEncoding.ENCODING_PCM_FLOAT -> "PCM_FLOAT"
}
