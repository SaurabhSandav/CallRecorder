package com.redridgeapps.callrecorder.screen.settings.ui

import androidx.compose.runtime.Composable
import com.redridgeapps.callrecorder.screen.common.pref.PreferenceCategory
import com.redridgeapps.callrecorder.screen.common.pref.SingleSelectListPreference
import com.redridgeapps.callrecorder.screen.settings.Preference
import com.redridgeapps.callrecorder.screen.settings.collectPrefValue
import com.redridgeapps.prefs.Prefs

@Composable
internal fun RecordingPreference(
    audioRecordSampleRate: Preference<Prefs.AudioRecord.SampleRate>,
    audioRecordChannels: Preference<Prefs.AudioRecord.Channels>,
    audioRecordEncoding: Preference<Prefs.AudioRecord.Encoding>,
) {

    PreferenceCategory(title = "Auto delete") {

        SingleSelectListPreference(
            title = "Sample Rate",
            keys = Prefs.AudioRecord.SampleRate.values().asList(),
            itemLabel = ::getSampleRateLabel,
            selectedItem = audioRecordSampleRate.collectPrefValue(),
            onSelectedChange = audioRecordSampleRate.onChanged
        )

        SingleSelectListPreference(
            title = "Channels",
            keys = Prefs.AudioRecord.Channels.values().asList(),
            itemLabel = ::getChannelsLabel,
            selectedItem = audioRecordChannels.collectPrefValue(),
            onSelectedChange = audioRecordChannels.onChanged
        )

        SingleSelectListPreference(
            title = "Encoding",
            keys = Prefs.AudioRecord.Encoding.values().asList(),
            itemLabel = ::getEncodingLabel,
            selectedItem = audioRecordEncoding.collectPrefValue(),
            onSelectedChange = audioRecordEncoding.onChanged
        )
    }
}

private fun getSampleRateLabel(key: Prefs.AudioRecord.SampleRate): String = when (key) {
    Prefs.AudioRecord.SampleRate.S8_000 -> "8,000"
    Prefs.AudioRecord.SampleRate.S11_025 -> "11,025"
    Prefs.AudioRecord.SampleRate.S16_000 -> "16,000"
    Prefs.AudioRecord.SampleRate.S22_050 -> "22,050"
    Prefs.AudioRecord.SampleRate.S44_100 -> "44,100"
    Prefs.AudioRecord.SampleRate.S48_000 -> "48,000"
}

private fun getChannelsLabel(key: Prefs.AudioRecord.Channels): String = when (key) {
    Prefs.AudioRecord.Channels.MONO -> "Mono"
    Prefs.AudioRecord.Channels.STEREO -> "Stereo"
}

private fun getEncodingLabel(key: Prefs.AudioRecord.Encoding): String = when (key) {
    Prefs.AudioRecord.Encoding.PCM_8BIT -> "PCM_8BIT"
    Prefs.AudioRecord.Encoding.PCM_16BIT -> "PCM_16BIT"
    Prefs.AudioRecord.Encoding.PCM_FLOAT -> "PCM_FLOAT"
}
