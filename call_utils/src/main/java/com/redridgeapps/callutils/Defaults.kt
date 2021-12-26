package com.redridgeapps.callutils

import com.redridgeapps.callutils.recording.AudioRecord

object Defaults {

    const val RECORDING_ENABLED = false

    val AUDIO_RECORD_SAMPLE_RATE = AudioRecord.SampleRate.S44_100
    val AUDIO_RECORD_CHANNELS = AudioRecord.Channels.MONO
    val AUDIO_RECORD_ENCODING = AudioRecord.Encoding.PCM_16BIT

    const val IS_AUTO_DELETE_ENABLED = false
    const val AUTO_DELETE_AFTER_DAYS = 30
}
