package com.redridgeapps.callrecorder.ui.settings

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redridgeapps.callrecorder.callutils.recording.PcmChannels
import com.redridgeapps.callrecorder.callutils.recording.PcmEncoding
import com.redridgeapps.callrecorder.callutils.recording.PcmSampleRate
import com.redridgeapps.callrecorder.callutils.storage.Recordings
import com.redridgeapps.callrecorder.prefs.MyPrefs
import com.redridgeapps.callrecorder.prefs.Prefs
import com.redridgeapps.callrecorder.utils.Systemizer
import com.redridgeapps.callrecorder.utils.constants.Defaults
import com.redridgeapps.callrecorder.utils.launchUnit
import kotlinx.coroutines.flow.first

class SettingsViewModel @ViewModelInject constructor(
    private val prefs: Prefs,
    private val systemizer: Systemizer,
    private val recordings: Recordings
) : ViewModel() {

    val uiState: SettingsState = SettingsState(
        isSystemized = systemizer.isAppSystemizedFlow,
        isRecordingOn = prefs.getFlow(MyPrefs.IS_RECORDING_ON) { Defaults.IS_RECORDING_ON },
        audioRecordSampleRate = prefs.getFlow(MyPrefs.AUDIO_RECORD_SAMPLE_RATE) { Defaults.AUDIO_RECORD_SAMPLE_RATE },
        audioRecordChannels = prefs.getFlow(MyPrefs.AUDIO_RECORD_CHANNELS) { Defaults.AUDIO_RECORD_CHANNELS },
        audioRecordEncoding = prefs.getFlow(MyPrefs.AUDIO_RECORD_ENCODING) { Defaults.AUDIO_RECORD_ENCODING }
    )

    fun flipSystemization() = viewModelScope.launchUnit {

        if (systemizer.isAppSystemizedFlow.first())
            systemizer.unSystemize()
        else
            systemizer.systemize()
    }

    fun flipRecording() = viewModelScope.launchUnit {
        val flippedIsRecording = !prefs.get(MyPrefs.IS_RECORDING_ON) { Defaults.IS_RECORDING_ON }
        prefs.set(MyPrefs.IS_RECORDING_ON, flippedIsRecording)
    }

    fun updateContactNames() = viewModelScope.launchUnit {
        recordings.updateContactNames()
    }

    fun setAudioRecordSampleRate(
        audioRecordSampleRate: PcmSampleRate
    ) = viewModelScope.launchUnit {
        prefs.set(MyPrefs.AUDIO_RECORD_SAMPLE_RATE, audioRecordSampleRate)
    }

    fun setAudioRecordChannels(
        audioRecordChannels: PcmChannels
    ) = viewModelScope.launchUnit {
        prefs.set(MyPrefs.AUDIO_RECORD_CHANNELS, audioRecordChannels)
    }

    fun setAudioRecordEncoding(
        audioRecordEncoding: PcmEncoding
    ) = viewModelScope.launchUnit {
        prefs.set(MyPrefs.AUDIO_RECORD_ENCODING, audioRecordEncoding)
    }
}
