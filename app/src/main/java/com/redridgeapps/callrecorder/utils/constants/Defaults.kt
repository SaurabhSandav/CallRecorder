package com.redridgeapps.callrecorder.utils.constants

import com.redridgeapps.callrecorder.callutils.recording.PcmChannels
import com.redridgeapps.callrecorder.callutils.recording.PcmEncoding
import com.redridgeapps.callrecorder.callutils.recording.PcmSampleRate

object Defaults {

    const val IS_RECORDING_ON = false

    val AUDIO_RECORD_SAMPLE_RATE = PcmSampleRate.S44_100
    val AUDIO_RECORD_CHANNELS = PcmChannels.MONO
    val AUDIO_RECORD_ENCODING = PcmEncoding.PCM_16BIT
}
