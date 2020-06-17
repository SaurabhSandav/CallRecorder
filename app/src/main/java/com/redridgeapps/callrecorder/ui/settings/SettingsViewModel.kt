package com.redridgeapps.callrecorder.ui.settings

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redridgeapps.callrecorder.callutils.RecordingDefaults
import com.redridgeapps.callrecorder.callutils.recording.PcmChannels
import com.redridgeapps.callrecorder.callutils.recording.PcmEncoding
import com.redridgeapps.callrecorder.callutils.recording.PcmSampleRate
import com.redridgeapps.callrecorder.callutils.storage.Recordings
import com.redridgeapps.callrecorder.common.utils.launchUnit
import com.redridgeapps.callrecorder.prefs.*
import com.redridgeapps.callrecorder.utils.Systemizer
import com.redridgeapps.callrecorder.utils.constants.Defaults
import kotlinx.coroutines.flow.first

class SettingsViewModel @ViewModelInject constructor(
    private val prefs: Prefs,
    private val systemizer: Systemizer,
    private val recordings: Recordings
) : ViewModel() {

    val uiState: SettingsState = SettingsState(
        isSystemized = systemizer.isAppSystemizedFlow,
        isRecordingOn = prefs.prefBoolean(PREF_IS_RECORDING_ON) { Defaults.IS_RECORDING_ON },
        audioRecordSampleRate = prefs.prefEnum(PREF_AUDIO_RECORD_SAMPLE_RATE) { RecordingDefaults.AUDIO_RECORD_SAMPLE_RATE },
        audioRecordChannels = prefs.prefEnum(PREF_AUDIO_RECORD_CHANNELS) { RecordingDefaults.AUDIO_RECORD_CHANNELS },
        audioRecordEncoding = prefs.prefEnum(PREF_AUDIO_RECORD_ENCODING) { RecordingDefaults.AUDIO_RECORD_ENCODING }
    )

    fun flipSystemization() = viewModelScope.launchUnit {

        if (systemizer.isAppSystemizedFlow.first())
            systemizer.unSystemize()
        else
            systemizer.systemize()
    }

    fun flipRecording() = viewModelScope.launchUnit {
        val flippedIsRecording =
            !prefs.prefBoolean(PREF_IS_RECORDING_ON) { Defaults.IS_RECORDING_ON }.first()
        prefs.editor { setBoolean(PREF_IS_RECORDING_ON, flippedIsRecording) }
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
