package com.redridgeapps.ui.settings

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redridgeapps.callutils.Defaults
import com.redridgeapps.callutils.recording.PcmChannels
import com.redridgeapps.callutils.recording.PcmEncoding
import com.redridgeapps.callutils.recording.PcmSampleRate
import com.redridgeapps.callutils.storage.Recordings
import com.redridgeapps.common.Systemizer
import com.redridgeapps.common.utils.launchUnit
import com.redridgeapps.prefs.PREF_AUDIO_RECORD_CHANNELS
import com.redridgeapps.prefs.PREF_AUDIO_RECORD_ENCODING
import com.redridgeapps.prefs.PREF_AUDIO_RECORD_SAMPLE_RATE
import com.redridgeapps.prefs.PREF_AUTO_DELETE_AFTER_DAYS
import com.redridgeapps.prefs.PREF_AUTO_DELETE_ENABLED
import com.redridgeapps.prefs.PREF_RECORDING_ENABLED
import com.redridgeapps.prefs.Prefs
import com.redridgeapps.prefs.enum
import com.redridgeapps.prefs.set
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal class SettingsViewModel @ViewModelInject constructor(
    private val prefs: Prefs,
    private val systemizer: Systemizer,
    private val recordings: Recordings,
) : ViewModel() {

    private val initialState = SettingsState(
        isAppSystemized = false onChange this::onAppSystemizationChanged,
        recordingEnabled = false onChange this::onRecordingEnabledChanged,
        onUpdateContactNames = this::updateContactNames,
        audioRecordSampleRate = Defaults.AUDIO_RECORD_SAMPLE_RATE onChange this::onAudioRecordSampleRateChanged,
        audioRecordChannels = Defaults.AUDIO_RECORD_CHANNELS onChange this::onAudioRecordChannelsChanged,
        audioRecordEncoding = Defaults.AUDIO_RECORD_ENCODING onChange this::onAudioRecordEncodingChanged,
        autoDeleteEnabled = false onChange this::onAutoDeleteEnabledChanged,
        autoDeleteAfterDays = -1 onChange this::onAutoDeleteAfterDaysChanged
    )
    private val _uiState = MutableStateFlow(initialState)

    val uiState: StateFlow<SettingsState> by ::_uiState

    init {
        collectPrefs()
    }

    private fun onAppSystemizationChanged(systemize: Boolean) = viewModelScope.launchUnit {
        if (systemize) systemizer.systemize() else systemizer.unSystemize()
    }

    private fun onRecordingEnabledChanged(newValue: Boolean) = viewModelScope.launchUnit {
        prefs.editor { set(PREF_RECORDING_ENABLED, newValue) }
    }

    private fun updateContactNames() = viewModelScope.launchUnit {
        recordings.updateContactNames()
    }

    private fun onAudioRecordSampleRateChanged(audioRecordSampleRate: PcmSampleRate) {
        prefs.editor { set(PREF_AUDIO_RECORD_SAMPLE_RATE, audioRecordSampleRate) }
    }

    private fun onAudioRecordChannelsChanged(audioRecordChannels: PcmChannels) {
        prefs.editor { set(PREF_AUDIO_RECORD_CHANNELS, audioRecordChannels) }
    }

    private fun onAudioRecordEncodingChanged(
        audioRecordEncoding: PcmEncoding,
    ) = viewModelScope.launchUnit {
        prefs.editor { set(PREF_AUDIO_RECORD_ENCODING, audioRecordEncoding) }
    }

    private fun onAutoDeleteEnabledChanged(newValue: Boolean) = viewModelScope.launchUnit {
        prefs.editor { set(PREF_AUTO_DELETE_ENABLED, newValue) }
    }

    private fun onAutoDeleteAfterDaysChanged(days: Int) = viewModelScope.launchUnit {
        prefs.editor { set(PREF_AUTO_DELETE_AFTER_DAYS, days) }
    }

    private fun collectPrefs() {

        fun <T> Flow<T>.onEachSetState(newValue: SettingsState.(T) -> SettingsState) {
            onEach { _uiState.value = _uiState.value.newValue(it) }.launchIn(viewModelScope)
        }

        systemizer.isAppSystemizedFlow.onEachSetState {
            copy(isAppSystemized = isAppSystemized.copy(it))
        }

        prefs.boolean(PREF_RECORDING_ENABLED) { Defaults.RECORDING_ENABLED }
            .onEachSetState { copy(recordingEnabled = recordingEnabled.copy(it)) }

        prefs.enum(PREF_AUDIO_RECORD_SAMPLE_RATE) { Defaults.AUDIO_RECORD_SAMPLE_RATE }
            .onEachSetState { copy(audioRecordSampleRate = audioRecordSampleRate.copy(it)) }

        prefs.enum(PREF_AUDIO_RECORD_CHANNELS) { Defaults.AUDIO_RECORD_CHANNELS }
            .onEachSetState { copy(audioRecordChannels = audioRecordChannels.copy(it)) }

        prefs.enum(PREF_AUDIO_RECORD_ENCODING) { Defaults.AUDIO_RECORD_ENCODING }
            .onEachSetState { copy(audioRecordEncoding = audioRecordEncoding.copy(it)) }

        prefs.boolean(PREF_AUTO_DELETE_ENABLED) { Defaults.AUTO_DELETE_ENABLED }
            .onEachSetState { copy(autoDeleteEnabled = autoDeleteEnabled.copy(it)) }

        prefs.int(PREF_AUTO_DELETE_AFTER_DAYS) { Defaults.AUTO_DELETE_AFTER_DAYS }
            .onEachSetState { copy(autoDeleteAfterDays = autoDeleteAfterDays.copy(it)) }
    }
}
