package com.redridgeapps.callrecorder.utils

import com.redridgeapps.callrecorder.callutils.PcmChannels
import com.redridgeapps.callrecorder.callutils.PcmEncoding
import com.redridgeapps.callrecorder.callutils.PcmSampleRate

object Defaults {

    const val IS_RECORDING_ON = false

    val AUDIO_RECORD_SAMPLE_RATE = PcmSampleRate.S44_100
    val AUDIO_RECORD_CHANNELS = PcmChannels.MONO
    val AUDIO_RECORD_ENCODING = PcmEncoding.PCM_16BIT
}