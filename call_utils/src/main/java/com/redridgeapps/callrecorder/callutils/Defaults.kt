package com.redridgeapps.callrecorder.callutils

import com.redridgeapps.callrecorder.callutils.recording.PcmChannels
import com.redridgeapps.callrecorder.callutils.recording.PcmEncoding
import com.redridgeapps.callrecorder.callutils.recording.PcmSampleRate

object Defaults {

    const val RECORDING_ENABLED = false

    val AUDIO_RECORD_SAMPLE_RATE = PcmSampleRate.S44_100
    val AUDIO_RECORD_CHANNELS = PcmChannels.MONO
    val AUDIO_RECORD_ENCODING = PcmEncoding.PCM_16BIT

    const val RECORDING_AUTO_DELETE_ENABLED = false
    const val RECORDING_AUTO_DELETE_AFTER_DAYS = 30
}
