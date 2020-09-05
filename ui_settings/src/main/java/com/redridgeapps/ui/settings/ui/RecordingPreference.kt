package com.redridgeapps.ui.settings.ui

import androidx.compose.runtime.Composable
import com.redridgeapps.callutils.recording.PcmChannels
import com.redridgeapps.callutils.recording.PcmEncoding
import com.redridgeapps.callutils.recording.PcmSampleRate
import com.redridgeapps.ui.common.pref.PreferenceCategory
import com.redridgeapps.ui.common.pref.SingleSelectListPreference
import com.redridgeapps.ui.settings.PreferenceValue

@Composable
internal fun RecordingPreference(
    audioRecordSampleRate: PreferenceValue<PcmSampleRate>,
    audioRecordChannels: PreferenceValue<PcmChannels>,
    audioRecordEncoding: PreferenceValue<PcmEncoding>,
) {

    PreferenceCategory(title = "Auto delete") {

        SingleSelectListPreference(
            title = "Sample Rate",
            keys = PcmSampleRate.values().asList(),
            itemLabel = { it.sampleRate.toString() },
            selectedItem = audioRecordSampleRate.value,
            onSelectedChange = audioRecordSampleRate.onChanged
        )

        SingleSelectListPreference(
            title = "Channels",
            keys = PcmChannels.values().asList(),
            itemLabel = {
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
            itemLabel = {
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
