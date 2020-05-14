package com.redridgeapps.callrecorder.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redridgeapps.callrecorder.callutils.PcmChannels
import com.redridgeapps.callrecorder.callutils.PcmEncoding
import com.redridgeapps.callrecorder.callutils.PcmSampleRate
import com.redridgeapps.callrecorder.callutils.Recordings
import com.redridgeapps.callrecorder.utils.Systemizer
import com.redridgeapps.callrecorder.utils.launchNoJob
import com.redridgeapps.callrecorder.utils.prefs.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val prefs: Prefs,
    private val systemizer: Systemizer,
    private val recordings: Recordings
) : ViewModel() {

    val uiState: SettingsState = SettingsState()

    init {

        observePref(PREF_IS_RECORDING_ON) { uiState.isRecordingOn = it }
        observePref(PREF_AUDIO_RECORD_SAMPLE_RATE) { uiState.audioRecordSampleRate = it }
        observePref(PREF_AUDIO_RECORD_CHANNELS) { uiState.audioRecordChannels = it }
        observePref(PREF_AUDIO_RECORD_ENCODING) { uiState.audioRecordEncoding = it }

        systemizer.isAppSystemizedFlow
            .onEach { uiState.isSystemized = it }
            .launchIn(viewModelScope)
    }

    fun flipSystemization() = viewModelScope.launchNoJob {

        uiState.isSystemized = null

        if (systemizer.isAppSystemizedFlow.first())
            systemizer.unSystemize()
        else
            systemizer.systemize()
    }

    fun flipRecording() = viewModelScope.launchNoJob {

        uiState.isRecordingOn = null

        val flippedIsRecording = !prefs.get(PREF_IS_RECORDING_ON)
        prefs.set(PREF_IS_RECORDING_ON, flippedIsRecording)
    }

    fun updateContactNames() = viewModelScope.launchNoJob {
        recordings.updateContactNames()
    }

    fun setAudioRecordSampleRate(
        audioRecordSampleRate: PcmSampleRate
    ) = viewModelScope.launchNoJob {

        uiState.audioRecordSampleRate = null

        prefs.set(PREF_AUDIO_RECORD_SAMPLE_RATE, audioRecordSampleRate)
    }

    fun setAudioRecordChannels(
        audioRecordChannels: PcmChannels
    ) = viewModelScope.launchNoJob {

        uiState.audioRecordChannels = null

        prefs.set(PREF_AUDIO_RECORD_CHANNELS, audioRecordChannels)
    }

    fun setAudioRecordEncoding(
        audioRecordEncoding: PcmEncoding
    ) = viewModelScope.launchNoJob {

        uiState.audioRecordEncoding = null

        prefs.set(PREF_AUDIO_RECORD_ENCODING, audioRecordEncoding)
    }

    private fun <T> observePref(pref: TypedPref<T>, action: suspend (T) -> Unit) {
        prefs.getFlow(pref)
            .onEach(action)
            .launchIn(viewModelScope)
    }
}
