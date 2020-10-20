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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

internal class SettingsViewModel @ViewModelInject constructor(
    private val prefs: DataStore<Prefs>,
    private val systemizer: Systemizer,
    private val recordings: Recordings,
) : ViewModel() {

    private val initialState = SettingsState(

        isAppSystemized = Preference(
            value = systemizer.isAppSystemizedFlow.toStateFlow(false),
            onChanged = launchable { if (it) systemizer.systemize() else systemizer.unSystemize() }
        ),

        recordingEnabled = Preference(
            value = prefValue(Defaults.RECORDING_ENABLED) { is_recording_enabled },
            onChanged = updatePrefs { copy(is_recording_enabled = it) }
        ),

        onUpdateContactNames = {
            viewModelScope.launchUnit { recordings.updateContactNames() }
        },

        audioRecordSampleRate = Preference(
            value = prefValue(Defaults.AUDIO_RECORD_SAMPLE_RATE) { audioRecord.sample_rate },
            onChanged = updatePrefs { copy(audio_record = audioRecord.copy(sample_rate = it)) }
        ),

        audioRecordChannels = Preference(
            value = prefValue(Defaults.AUDIO_RECORD_CHANNELS) { audioRecord.channels },
            onChanged = updatePrefs { copy(audio_record = audioRecord.copy(channels = it)) }
        ),

        audioRecordEncoding = Preference(
            value = prefValue(Defaults.AUDIO_RECORD_ENCODING) { audioRecord.encoding },
            onChanged = updatePrefs { copy(audio_record = audioRecord.copy(encoding = it)) }
        ),

        autoDeleteEnabled = Preference(
            value = prefValue(Defaults.AUTO_DELETE_ENABLED) { is_auto_delete_enabled },
            onChanged = updatePrefs { copy(is_auto_delete_enabled = it) }
        ),

        autoDeleteAfterDays = Preference(
            value = prefValue(Defaults.AUTO_DELETE_AFTER_DAYS) { auto_delete_threshold_days },
            onChanged = updatePrefs { copy(auto_delete_threshold_days = it) }
        ),
    )

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<SettingsState> by ::_uiState

    private fun <T> Flow<T>.toStateFlow(initialValue: T): StateFlow<T> = stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = initialValue
    )

    private inline fun <T> prefValue(
        initialValue: T,
        crossinline transform: suspend Prefs.() -> T,
    ): StateFlow<T> = prefs.data.map { it.transform() }.toStateFlow(initialValue)

    private inline fun <T> launchable(crossinline block: suspend (T) -> Unit): (T) -> Unit = {
        viewModelScope.launchUnit { block(it) }
    }

    private inline fun <T> updatePrefs(
        crossinline block: suspend Prefs.(T) -> Prefs,
    ): (T) -> Unit = launchable {
        prefs.updateData { prefsSnapshot -> prefsSnapshot.block(it) }
    }
}
