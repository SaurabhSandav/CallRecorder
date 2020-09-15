package com.redridgeapps.callutils

import com.redridgeapps.prefs.Prefs

object Defaults {

    const val RECORDING_ENABLED = false

    val AUDIO_RECORD_SAMPLE_RATE = Prefs.AudioRecord.SampleRate.S44_100
    val AUDIO_RECORD_CHANNELS = Prefs.AudioRecord.Channels.MONO
    val AUDIO_RECORD_ENCODING = Prefs.AudioRecord.Encoding.PCM_16BIT

    const val AUTO_DELETE_ENABLED = false
    const val AUTO_DELETE_AFTER_DAYS = 30
}
