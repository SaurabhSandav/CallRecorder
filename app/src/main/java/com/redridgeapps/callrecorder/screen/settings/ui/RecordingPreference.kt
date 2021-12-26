package com.redridgeapps.callrecorder.screen.settings.ui

import androidx.compose.runtime.Composable
import com.redridgeapps.callrecorder.screen.common.pref.PreferenceCategory
import com.redridgeapps.callrecorder.screen.common.pref.SingleSelectListPreference
import com.redridgeapps.callrecorder.screen.settings.Preference
import com.redridgeapps.callrecorder.screen.settings.collectPrefValue
import com.redridgeapps.callutils.recording.AudioRecord

@Composable
internal fun RecordingPreference(
    audioRecordSampleRate: Preference<Int>,
    audioRecordChannels: Preference<Int>,
    audioRecordEncoding: Preference<Int>,
) {

    PreferenceCategory(title = "Auto delete") {

        SingleSelectListPreference(
            title = "Sample Rate",
            keys = AudioRecord.SampleRate.values().asList(),
            itemLabel = { getSampleRateLabel(it) },
            selectedItem = AudioRecord.SampleRate.values()
                .first { it.value == audioRecordSampleRate.collectPrefValue() },
            onSelectedChange = { audioRecordSampleRate.onChanged(it.value) }
        )

        SingleSelectListPreference(
            title = "Channels",
            keys = AudioRecord.Channels.values().asList(),
            itemLabel = ::getChannelsLabel,
            selectedItem = AudioRecord.Channels.values().first { it.value == audioRecordChannels.collectPrefValue() },
            onSelectedChange = { audioRecordChannels.onChanged(it.value) }
        )

        SingleSelectListPreference(
            title = "Encoding",
            keys = AudioRecord.Encoding.values().asList(),
            itemLabel = ::getEncodingLabel,
            selectedItem = AudioRecord.Encoding.values().first { it.value == audioRecordEncoding.collectPrefValue() },
            onSelectedChange = { audioRecordEncoding.onChanged(it.value) }
        )
    }
}

private fun getSampleRateLabel(key: AudioRecord.SampleRate): String = when (key) {
    AudioRecord.SampleRate.S8_000 -> "8,000"
    AudioRecord.SampleRate.S11_025 -> "11,025"
    AudioRecord.SampleRate.S16_000 -> "16,000"
    AudioRecord.SampleRate.S22_050 -> "22,050"
    AudioRecord.SampleRate.S44_100 -> "44,100"
    AudioRecord.SampleRate.S48_000 -> "48,000"
}

private fun getChannelsLabel(key: AudioRecord.Channels): String = when (key) {
    AudioRecord.Channels.MONO -> "Mono"
    AudioRecord.Channels.STEREO -> "Stereo"
}

private fun getEncodingLabel(key: AudioRecord.Encoding): String = when (key) {
    AudioRecord.Encoding.PCM_8BIT -> "PCM_8BIT"
    AudioRecord.Encoding.PCM_16BIT -> "PCM_16BIT"
    AudioRecord.Encoding.PCM_FLOAT -> "PCM_FLOAT"
}
