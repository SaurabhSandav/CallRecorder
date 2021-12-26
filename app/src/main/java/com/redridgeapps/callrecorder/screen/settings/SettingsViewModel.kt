package com.redridgeapps.callrecorder.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redridgeapps.callutils.Defaults
import com.redridgeapps.callutils.storage.Recordings
import com.redridgeapps.common.PrefKeys
import com.redridgeapps.common.Systemizer
import com.redridgeapps.common.utils.launchUnit
import com.russhwolf.settings.coroutines.FlowSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
internal class SettingsViewModel @Inject constructor(
    private val prefs: FlowSettings,
    private val systemizer: Systemizer,
    private val recordings: Recordings,
) : ViewModel() {

    private val initialState = SettingsState(

        isAppSystemized = Preference(
            value = systemizer.isAppSystemizedFlow.toStateFlow(false),
            onChanged = launchable { if (it) systemizer.systemize() else systemizer.unSystemize() }
        ),

        recordingEnabled = Preference(
            value = prefValue(PrefKeys.isRecordingEnabled, Defaults.RECORDING_ENABLED),
            onChanged = launchable { prefs.putBoolean(PrefKeys.isRecordingEnabled, it) }
        ),

        onUpdateContactNames = {
            viewModelScope.launchUnit { recordings.updateContactNames() }
        },

        audioRecordSampleRate = Preference(
            value = prefValue(PrefKeys.AudioRecord.sampleRate, Defaults.AUDIO_RECORD_SAMPLE_RATE.value),
            onChanged = launchable { prefs.putInt(PrefKeys.AudioRecord.sampleRate, it) },
        ),

        audioRecordChannels = Preference(
            value = prefValue(PrefKeys.AudioRecord.channels, Defaults.AUDIO_RECORD_CHANNELS.value),
            onChanged = launchable { prefs.putInt(PrefKeys.AudioRecord.channels, it) },
        ),

        audioRecordEncoding = Preference(
            value = prefValue(PrefKeys.AudioRecord.encoding, Defaults.AUDIO_RECORD_ENCODING.value),
            onChanged = launchable { prefs.putInt(PrefKeys.AudioRecord.encoding, it) },
        ),

        autoDeleteEnabled = Preference(
            value = prefValue(PrefKeys.isAutoDeleteEnabled, Defaults.IS_AUTO_DELETE_ENABLED),
            onChanged = launchable { prefs.putBoolean(PrefKeys.isAutoDeleteEnabled, it) }
        ),

        autoDeleteAfterDays = Preference(
            value = prefValue(PrefKeys.autoDeleteThresholdDays, Defaults.AUTO_DELETE_AFTER_DAYS),
            onChanged = launchable { prefs.putInt(PrefKeys.autoDeleteThresholdDays, it) }
        ),
    )

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<SettingsState> by ::_uiState

    private fun <T> Flow<T>.toStateFlow(initialValue: T): StateFlow<T> = stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = initialValue
    )

    private inline fun <reified T> prefValue(
        key: String,
        initialValue: T,
    ): StateFlow<T> {

        val flow = when (T::class) {
            Boolean::class -> prefs.getBooleanFlow(key, initialValue as Boolean)
            Int::class -> prefs.getIntFlow(key, initialValue as Int)
            Long::class -> prefs.getLongFlow(key, initialValue as Long)
            Float::class -> prefs.getFloatFlow(key, initialValue as Float)
            Double::class -> prefs.getDoubleFlow(key, initialValue as Double)
            String::class -> prefs.getStringFlow(key, initialValue as String)
            else -> error("Flow")
        }

        return flow.toStateFlow(initialValue) as StateFlow<T>
    }

    private inline fun <T> launchable(crossinline block: suspend (T) -> Unit): (T) -> Unit = {
        viewModelScope.launchUnit { block(it) }
    }
}
