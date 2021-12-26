package com.redridgeapps.callutils.recording

import android.media.AudioFormat
import com.redridgeapps.wavutils.WavBitsPerSample
import com.redridgeapps.wavutils.WavChannels

fun WavChannels.toAudioRecordChannel(): Int = when (this.value) {
    1 -> AudioFormat.CHANNEL_IN_MONO
    2 -> AudioFormat.CHANNEL_IN_STEREO
    else -> error("Unexpected channels")
}

fun WavBitsPerSample.toAudioRecordEncoding(): Int = when (this.value) {
    8 -> AudioFormat.ENCODING_PCM_8BIT
    16 -> AudioFormat.ENCODING_PCM_16BIT
    32 -> AudioFormat.ENCODING_PCM_FLOAT
    else -> error("Unexpected bitsPerSample")
}
