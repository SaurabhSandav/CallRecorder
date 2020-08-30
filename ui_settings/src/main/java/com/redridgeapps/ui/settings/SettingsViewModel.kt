package com.redridgeapps.ui.settings

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redridgeapps.callrecorder.callutils.Defaults
import com.redridgeapps.callrecorder.callutils.recording.PcmChannels
import com.redridgeapps.callrecorder.callutils.recording.PcmEncoding
import com.redridgeapps.callrecorder.callutils.recording.PcmSampleRate
import com.redridgeapps.callrecorder.callutils.storage.Recordings
import com.redridgeapps.callrecorder.common.Systemizer
import com.redridgeapps.callrecorder.common.utils.launchUnit
import com.redridgeapps.callrecorder.prefs.PREF_AUDIO_RECORD_CHANNELS
import com.redridgeapps.callrecorder.prefs.PREF_AUDIO_RECORD_ENCODING
import com.redridgeapps.callrecorder.prefs.PREF_AUDIO_RECORD_SAMPLE_RATE
import com.redridgeapps.callrecorder.prefs.PREF_RECORDING_AUTO_DELETE_AFTER_DAYS
import com.redridgeapps.callrecorder.prefs.PREF_RECORDING_AUTO_DELETE_ENABLED
import com.redridgeapps.callrecorder.prefs.PREF_RECORDING_ENABLED
import com.redridgeapps.callrecorder.prefs.Prefs
import com.redridgeapps.callrecorder.prefs.enum
import com.redridgeapps.callrecorder.prefs.set
import kotlinx.coroutines.flow.first

internal class SettingsViewModel @ViewModelInject constructor(
    private val prefs: Prefs,
    private val systemizer: Systemizer,
    private val recordings: Recordings
) : ViewModel() {

    val uiState: SettingsState = SettingsState(
        isSystemized = systemizer.isAppSystemizedFlow,
        recordingEnabled = prefs.boolean(PREF_RECORDING_ENABLED) { Defaults.RECORDING_ENABLED },
        audioRecordSampleRate = prefs.enum(PREF_AUDIO_RECORD_SAMPLE_RATE) {
            Defaults.AUDIO_RECORD_SAMPLE_RATE
        },
        audioRecordChannels = prefs.enum(PREF_AUDIO_RECORD_CHANNELS) {
            Defaults.AUDIO_RECORD_CHANNELS
        },
        audioRecordEncoding = prefs.enum(PREF_AUDIO_RECORD_ENCODING) {
            Defaults.AUDIO_RECORD_ENCODING
        },
        recordingAutoDeleteEnabled = prefs.boolean(PREF_RECORDING_AUTO_DELETE_ENABLED) {
            Defaults.RECORDING_AUTO_DELETE_ENABLED
        },
        recordingAutoDeleteAfterDays = prefs.int(PREF_RECORDING_AUTO_DELETE_AFTER_DAYS) {
            Defaults.RECORDING_AUTO_DELETE_AFTER_DAYS
        }
    )

    fun flipSystemization() = viewModelScope.launchUnit {
        when {
            systemizer.isAppSystemizedFlow.first() -> systemizer.unSystemize()
            else -> systemizer.systemize()
        }
    }

    fun flipRecordingEnabled() = viewModelScope.launchUnit {
        val flipped = !prefs
            .boolean(PREF_RECORDING_ENABLED) { Defaults.RECORDING_ENABLED }
            .first()
        prefs.editor { set(PREF_RECORDING_ENABLED, flipped) }
    }

    fun updateContactNames() = viewModelScope.launchUnit {
        recordings.updateContactNames()
    }

    fun setAudioRecordSampleRate(audioRecordSampleRate: PcmSampleRate) {
        prefs.editor { set(PREF_AUDIO_RECORD_SAMPLE_RATE, audioRecordSampleRate) }
    }

    fun setAudioRecordChannels(audioRecordChannels: PcmChannels) {
        prefs.editor { set(PREF_AUDIO_RECORD_CHANNELS, audioRecordChannels) }
    }

    fun setAudioRecordEncoding(audioRecordEncoding: PcmEncoding) = viewModelScope.launchUnit {
        prefs.editor { set(PREF_AUDIO_RECORD_ENCODING, audioRecordEncoding) }
    }

    fun flipRecordingAutoDeleteEnabled() = viewModelScope.launchUnit {
        val flippedIsRecording = !prefs
            .boolean(PREF_RECORDING_AUTO_DELETE_ENABLED) { Defaults.RECORDING_AUTO_DELETE_ENABLED }
            .first()
        prefs.editor { set(PREF_RECORDING_AUTO_DELETE_ENABLED, flippedIsRecording) }
    }

    fun setRecordingAutoDeleteAfterDays(days: Int) = viewModelScope.launchUnit {
        prefs.editor { set(PREF_RECORDING_AUTO_DELETE_AFTER_DAYS, days) }
    }
}
