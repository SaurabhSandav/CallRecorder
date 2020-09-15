package com.redridgeapps.ui.settings

import androidx.datastore.DataStore
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redridgeapps.callutils.Defaults
import com.redridgeapps.callutils.storage.Recordings
import com.redridgeapps.common.Systemizer
import com.redridgeapps.common.utils.launchUnit
import com.redridgeapps.prefs.Prefs
import com.redridgeapps.prefs.audioRecord
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

internal class SettingsViewModel @ViewModelInject constructor(
    private val prefs: DataStore<Prefs>,
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
            value = prefs.data.map { it.is_recording_enabled },
            initialValue = Defaults.RECORDING_ENABLED,
            onChanged = updatePrefs { copy(is_recording_enabled = it) }
        ),

        onUpdateContactNames = {
            viewModelScope.launchUnit { recordings.updateContactNames() }
        },

        audioRecordSampleRate = Preference(
            value = prefs.data.map { it.audioRecord.sample_rate },
            initialValue = Defaults.AUDIO_RECORD_SAMPLE_RATE,
            onChanged = updatePrefs { copy(audio_record = audioRecord.copy(sample_rate = it)) }
        ),

        audioRecordChannels = Preference(
            value = prefs.data.map { it.audioRecord.channels },
            initialValue = Defaults.AUDIO_RECORD_CHANNELS,
            onChanged = updatePrefs { copy(audio_record = audioRecord.copy(channels = it)) }
        ),

        audioRecordEncoding = Preference(
            value = prefs.data.map { it.audioRecord.encoding },
            initialValue = Defaults.AUDIO_RECORD_ENCODING,
            onChanged = updatePrefs { copy(audio_record = audioRecord.copy(encoding = it)) }
        ),

        autoDeleteEnabled = Preference(
            value = prefs.data.map { it.is_auto_delete_enabled },
            initialValue = Defaults.AUTO_DELETE_ENABLED,
            onChanged = updatePrefs { copy(is_auto_delete_enabled = it) }
        ),

        autoDeleteAfterDays = Preference(
            value = prefs.data.map { it.auto_delete_threshold_days },
            initialValue = Defaults.AUTO_DELETE_AFTER_DAYS,
            onChanged = updatePrefs { copy(auto_delete_threshold_days = it) }
        ),
    )

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<SettingsState> by ::_uiState

    private inline fun <T> launchable(crossinline block: suspend (T) -> Unit): (T) -> Unit = {
        viewModelScope.launchUnit { block(it) }
    }

    private inline fun <T> updatePrefs(
        crossinline block: suspend Prefs.(T) -> Prefs,
    ): (T) -> Unit = launchable {
        prefs.updateData { prefsSnapshot -> prefsSnapshot.block(it) }
    }
}
