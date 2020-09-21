package com.redridgeapps.callutils.recording

import android.media.AudioFormat
import com.redridgeapps.prefs.Prefs
import com.redridgeapps.wavutils.WavBitsPerSample
import com.redridgeapps.wavutils.WavChannels
import com.redridgeapps.wavutils.WavSampleRate

fun Prefs.AudioRecord.SampleRate.toWavSampleRate(): WavSampleRate {
    return when (this) {
        Prefs.AudioRecord.SampleRate.S44_100 -> 44_100
        Prefs.AudioRecord.SampleRate.S8_000 -> 8_000
        Prefs.AudioRecord.SampleRate.S11_025 -> 11_025
        Prefs.AudioRecord.SampleRate.S16_000 -> 16_000
        Prefs.AudioRecord.SampleRate.S22_050 -> 22_050
        Prefs.AudioRecord.SampleRate.S48_000 -> 48_000
    }.let(::WavSampleRate)
}

fun Prefs.AudioRecord.Channels.toWavChannels(): WavChannels {
    return when (this) {
        Prefs.AudioRecord.Channels.MONO -> 1
        Prefs.AudioRecord.Channels.STEREO -> 2
    }.let(::WavChannels)
}

fun Prefs.AudioRecord.Encoding.toWavBitsPerSample(): WavBitsPerSample {
    return when (this) {
        Prefs.AudioRecord.Encoding.PCM_8BIT -> 8
        Prefs.AudioRecord.Encoding.PCM_16BIT -> 16
        Prefs.AudioRecord.Encoding.PCM_FLOAT -> 32
    }.let(::WavBitsPerSample)
}

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
