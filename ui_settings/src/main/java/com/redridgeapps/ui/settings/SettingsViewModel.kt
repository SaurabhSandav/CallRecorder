package com.redridgeapps.ui.settings

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redridgeapps.callutils.Defaults
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class SettingsViewModel @ViewModelInject constructor(
    private val prefs: Prefs,
    private val systemizer: Systemizer,
    private val recordings: Recordings,
) : ViewModel() {

    private val initialState = SettingsState(

        isAppSystemized = Preference(
            value = systemizer.isAppSystemizedFlow,
            initialValue = false,
            onChanged = launchable { if (it) systemizer.systemize() else systemizer.unSystemize() }
        ),

        recordingEnabled = Preference(
            value = prefs.boolean(PREF_RECORDING_ENABLED) { Defaults.RECORDING_ENABLED },
            initialValue = Defaults.RECORDING_ENABLED,
            onChanged = launchable { prefs.editor { set(PREF_RECORDING_ENABLED, it) } }
        ),

        onUpdateContactNames = {
            viewModelScope.launchUnit { recordings.updateContactNames() }
        },

        audioRecordSampleRate = Preference(
            value = prefs.enum(PREF_AUDIO_RECORD_SAMPLE_RATE) { Defaults.AUDIO_RECORD_SAMPLE_RATE },
            initialValue = Defaults.AUDIO_RECORD_SAMPLE_RATE,
            onChanged = { prefs.editor { set(PREF_AUDIO_RECORD_SAMPLE_RATE, it) } }
        ),

        audioRecordChannels = Preference(
            value = prefs.enum(PREF_AUDIO_RECORD_CHANNELS) { Defaults.AUDIO_RECORD_CHANNELS },
            initialValue = Defaults.AUDIO_RECORD_CHANNELS,
            onChanged = { prefs.editor { set(PREF_AUDIO_RECORD_CHANNELS, it) } }
        ),

        audioRecordEncoding = Preference(
            value = prefs.enum(PREF_AUDIO_RECORD_ENCODING) { Defaults.AUDIO_RECORD_ENCODING },
            initialValue = Defaults.AUDIO_RECORD_ENCODING,
            onChanged = launchable { prefs.editor { set(PREF_AUDIO_RECORD_ENCODING, it) } }
        ),

        autoDeleteEnabled = Preference(
            value = prefs.boolean(PREF_AUTO_DELETE_ENABLED) { Defaults.AUTO_DELETE_ENABLED },
            initialValue = Defaults.AUTO_DELETE_ENABLED,
            onChanged = launchable { prefs.editor { set(PREF_AUTO_DELETE_ENABLED, it) } }
        ),

        autoDeleteAfterDays = Preference(
            value = prefs.int(PREF_AUTO_DELETE_AFTER_DAYS) { Defaults.AUTO_DELETE_AFTER_DAYS },
            initialValue = Defaults.AUTO_DELETE_AFTER_DAYS,
            onChanged = launchable { prefs.editor { set(PREF_AUTO_DELETE_AFTER_DAYS, it) } }
        ),
    )

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<SettingsState> by ::_uiState

    private inline fun <T> launchable(crossinline block: suspend (T) -> Unit): (T) -> Unit = {
        viewModelScope.launchUnit { block(it) }
    }
}
