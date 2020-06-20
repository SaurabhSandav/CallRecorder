package com.redridgeapps.callrecorder.ui.settings

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redridgeapps.callrecorder.callutils.Defaults
import com.redridgeapps.callrecorder.callutils.recording.PcmChannels
import com.redridgeapps.callrecorder.callutils.recording.PcmEncoding
import com.redridgeapps.callrecorder.callutils.recording.PcmSampleRate
import com.redridgeapps.callrecorder.callutils.storage.Recordings
import com.redridgeapps.callrecorder.common.utils.launchUnit
import com.redridgeapps.callrecorder.prefs.*
import com.redridgeapps.callrecorder.utils.Systemizer
import kotlinx.coroutines.flow.first

class SettingsViewModel @ViewModelInject constructor(
    private val prefs: Prefs,
    private val systemizer: Systemizer,
    private val recordings: Recordings
) : ViewModel() {

    val uiState: SettingsState = SettingsState(
        isSystemized = systemizer.isAppSystemizedFlow,
        recordingEnabled = prefs.prefBoolean(PREF_RECORDING_ENABLED) {
            Defaults.RECORDING_ENABLED
        },
        audioRecordSampleRate = prefs.prefEnum(PREF_AUDIO_RECORD_SAMPLE_RATE) {
            Defaults.AUDIO_RECORD_SAMPLE_RATE
        },
        audioRecordChannels = prefs.prefEnum(PREF_AUDIO_RECORD_CHANNELS) {
            Defaults.AUDIO_RECORD_CHANNELS
        },
        audioRecordEncoding = prefs.prefEnum(PREF_AUDIO_RECORD_ENCODING) {
            Defaults.AUDIO_RECORD_ENCODING
        }
    )

    fun flipSystemization() = viewModelScope.launchUnit {
        when {
            systemizer.isAppSystemizedFlow.first() -> systemizer.unSystemize()
            else -> systemizer.systemize()
        }
    }

    fun flipRecordingEnabled() = viewModelScope.launchUnit {
        val flipped = !prefs.prefBoolean(PREF_RECORDING_ENABLED) {
            Defaults.RECORDING_ENABLED
        }.first()
        prefs.editor { setBoolean(PREF_RECORDING_ENABLED, flipped) }
    }

    fun updateContactNames() = viewModelScope.launchUnit {
        recordings.updateContactNames()
    }

    fun setAudioRecordSampleRate(
        audioRecordSampleRate: PcmSampleRate
    ) {
        prefs.editor { setEnum(PREF_AUDIO_RECORD_SAMPLE_RATE, audioRecordSampleRate) }
    }

    fun setAudioRecordChannels(
        audioRecordChannels: PcmChannels
    ) {
        prefs.editor { setEnum(PREF_AUDIO_RECORD_CHANNELS, audioRecordChannels) }
    }

    fun setAudioRecordEncoding(
        audioRecordEncoding: PcmEncoding
    ) = viewModelScope.launchUnit {
        prefs.editor { setEnum(PREF_AUDIO_RECORD_ENCODING, audioRecordEncoding) }
    }
}
