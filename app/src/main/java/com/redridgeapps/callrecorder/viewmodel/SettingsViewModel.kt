package com.redridgeapps.callrecorder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redridgeapps.callrecorder.utils.Systemizer
import com.redridgeapps.callrecorder.utils.prefs.*
import com.redridgeapps.repository.callutils.AudioRecordChannels
import com.redridgeapps.repository.callutils.AudioRecordEncoding
import com.redridgeapps.repository.callutils.AudioRecordSampleRate
import com.redridgeapps.repository.viewmodel.ISettingsViewModel
import com.redridgeapps.ui.SettingsState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val prefs: Prefs,
    private val systemizer: Systemizer
) : ViewModel(), ISettingsViewModel {

    override val uiState: SettingsState = SettingsState()

    init {

        observePref(PREF_IS_RECORDING_ON) { uiState.isRecordingOn = it }
        observePref(PREF_AUDIO_RECORD_SAMPLE_RATE) { uiState.audioRecordSampleRate = it }
        observePref(PREF_AUDIO_RECORD_CHANNELS) { uiState.audioRecordChannels = it }
        observePref(PREF_AUDIO_RECORD_ENCODING) { uiState.audioRecordEncoding = it }

        systemizer.isAppSystemizedFlow
            .onEach { uiState.isSystemized = it }
            .launchIn(viewModelScope)
    }

    override fun flipSystemization() {
        viewModelScope.launch {

            uiState.isSystemized = null

            if (systemizer.isAppSystemizedFlow.first())
                systemizer.unSystemize()
            else
                systemizer.systemize()
        }
    }

    override fun flipRecording() {
        viewModelScope.launch {

            uiState.isRecordingOn = null

            val flippedIsRecording = !prefs.get(PREF_IS_RECORDING_ON)
            prefs.set(PREF_IS_RECORDING_ON, flippedIsRecording)
        }
    }

    override fun setAudioRecordSampleRate(audioRecordSampleRate: AudioRecordSampleRate) {
        viewModelScope.launch {

            uiState.audioRecordSampleRate = null

            prefs.set(PREF_AUDIO_RECORD_SAMPLE_RATE, audioRecordSampleRate)
        }
    }

    override fun setAudioRecordChannels(audioRecordChannels: AudioRecordChannels) {
        viewModelScope.launch {

            uiState.audioRecordChannels = null

            prefs.set(PREF_AUDIO_RECORD_CHANNELS, audioRecordChannels)
        }
    }

    override fun setAudioRecordEncoding(audioRecordEncoding: AudioRecordEncoding) {
        viewModelScope.launch {

            uiState.audioRecordEncoding = null

            prefs.set(PREF_AUDIO_RECORD_ENCODING, audioRecordEncoding)
        }
    }

    private fun <T> observePref(pref: TypedPref<T>, action: suspend (T) -> Unit) {
        prefs.getFlow(pref)
            .onEach(action)
            .launchIn(viewModelScope)
    }
}
