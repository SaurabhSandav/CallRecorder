package com.redridgeapps.ui.settings.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.redridgeapps.callrecorder.callutils.recording.PcmChannels
import com.redridgeapps.callrecorder.callutils.recording.PcmEncoding
import com.redridgeapps.callrecorder.callutils.recording.PcmSampleRate
import com.redridgeapps.ui.common.pref.SingleSelectListPreference
import com.redridgeapps.ui.common.pref.TitlePreference
import com.redridgeapps.ui.settings.PreferenceValue

@Composable
internal fun RecordingPreference(
    audioRecordSampleRate: PreferenceValue<PcmSampleRate>,
    audioRecordChannels: PreferenceValue<PcmChannels>,
    audioRecordEncoding: PreferenceValue<PcmEncoding>,
) {

    Column {

        TitlePreference(text = "Recording")

        SingleSelectListPreference(
            title = "Sample Rate",
            keys = PcmSampleRate.values().asList(),
            itemText = { it.sampleRate.toString() },
            selectedItem = audioRecordSampleRate.value,
            onSelectedChange = audioRecordSampleRate.onChanged
        )

        SingleSelectListPreference(
            title = "Channels",
            keys = PcmChannels.values().asList(),
            itemText = {
                when (it) {
                    PcmChannels.MONO -> "Mono"
                    PcmChannels.STEREO -> "Stereo"
                }
            },
            selectedItem = audioRecordChannels.value,
            onSelectedChange = audioRecordChannels.onChanged
        )

        SingleSelectListPreference(
            title = "Encoding",
            keys = PcmEncoding.values().asList(),
            itemText = {
                when (it) {
                    PcmEncoding.PCM_8BIT -> "PCM_8BIT"
                    PcmEncoding.PCM_16BIT -> "PCM_16BIT"
                    PcmEncoding.PCM_FLOAT -> "PCM_FLOAT"
                }
            },
            selectedItem = audioRecordEncoding.value,
            onSelectedChange = audioRecordEncoding.onChanged
        )
    }
}
