package com.redridgeapps.callutils

import com.redridgeapps.callutils.recording.PcmChannels
import com.redridgeapps.callutils.recording.PcmEncoding
import com.redridgeapps.callutils.recording.PcmSampleRate

object Defaults {

    const val RECORDING_ENABLED = false

    val AUDIO_RECORD_SAMPLE_RATE = PcmSampleRate.S44_100
    val AUDIO_RECORD_CHANNELS = PcmChannels.MONO
    val AUDIO_RECORD_ENCODING = PcmEncoding.PCM_16BIT

    const val AUTO_DELETE_ENABLED = false
    const val AUTO_DELETE_AFTER_DAYS = 30
}
