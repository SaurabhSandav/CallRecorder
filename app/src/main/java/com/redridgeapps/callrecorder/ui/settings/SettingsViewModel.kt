package com.redridgeapps.callrecorder.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redridgeapps.callrecorder.callutils.PcmChannels
import com.redridgeapps.callrecorder.callutils.PcmEncoding
import com.redridgeapps.callrecorder.callutils.PcmSampleRate
import com.redridgeapps.callrecorder.callutils.Recordings
import com.redridgeapps.callrecorder.utils.Systemizer
import com.redridgeapps.callrecorder.utils.launchNoJob
import com.redridgeapps.callrecorder.utils.prefs.MyPrefs
import com.redridgeapps.callrecorder.utils.prefs.Prefs
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val prefs: Prefs,
    private val systemizer: Systemizer,
    private val recordings: Recordings
) : ViewModel() {

    val uiState: SettingsState = SettingsState(
        isSystemized = systemizer.isAppSystemizedFlow,
        isRecordingOn = prefs.getFlow(MyPrefs.IS_RECORDING_ON) { false },
        audioRecordSampleRate = prefs.getFlow(MyPrefs.AUDIO_RECORD_SAMPLE_RATE) { PcmSampleRate.S44_100 },
        audioRecordChannels = prefs.getFlow(MyPrefs.AUDIO_RECORD_CHANNELS) { PcmChannels.MONO },
        audioRecordEncoding = prefs.getFlow(MyPrefs.AUDIO_RECORD_ENCODING) { PcmEncoding.PCM_16BIT }
    )

    fun flipSystemization() = viewModelScope.launchNoJob {

        if (systemizer.isAppSystemizedFlow.first())
            systemizer.unSystemize()
        else
            systemizer.systemize()
    }

    fun flipRecording() = viewModelScope.launchNoJob {
        val flippedIsRecording = !prefs.get(MyPrefs.IS_RECORDING_ON) { false }
        prefs.set(MyPrefs.IS_RECORDING_ON, flippedIsRecording)
    }

    fun updateContactNames() = viewModelScope.launchNoJob {
        recordings.updateContactNames()
    }

    fun setAudioRecordSampleRate(
        audioRecordSampleRate: PcmSampleRate
    ) = viewModelScope.launchNoJob {
        prefs.set(MyPrefs.AUDIO_RECORD_SAMPLE_RATE, audioRecordSampleRate)
    }

    fun setAudioRecordChannels(
        audioRecordChannels: PcmChannels
    ) = viewModelScope.launchNoJob {
        prefs.set(MyPrefs.AUDIO_RECORD_CHANNELS, audioRecordChannels)
    }

    fun setAudioRecordEncoding(
        audioRecordEncoding: PcmEncoding
    ) = viewModelScope.launchNoJob {
        prefs.set(MyPrefs.AUDIO_RECORD_ENCODING, audioRecordEncoding)
    }
}
