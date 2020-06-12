package com.redridgeapps.callrecorder.callutils

import android.media.AudioFormat
import com.redridgeapps.wavutils.WavBitsPerSample
import com.redridgeapps.wavutils.WavChannels
import com.redridgeapps.wavutils.WavSampleRate

enum class PcmSampleRate(val sampleRate: Int) {
    S8_000(8_000),
    S11_025(11_025),
    S16_000(16_000),
    S22_050(22_050),
    S44_100(44_100),
    S48_000(48_000);
}

enum class PcmChannels(val channelCount: Int) {
    MONO(1),
    STEREO(2);
}

enum class PcmEncoding(val bitsPerSample: Int) {
    PCM_8BIT(8),
    PCM_16BIT(16),
    PCM_FLOAT(32);
}

fun PcmChannels.toAudioRecordChannel(): Int = when (this) {
    PcmChannels.MONO -> AudioFormat.CHANNEL_IN_MONO
    PcmChannels.STEREO -> AudioFormat.CHANNEL_IN_STEREO
}

fun PcmEncoding.toAudioRecordEncoding(): Int = when (this) {
    PcmEncoding.PCM_8BIT -> AudioFormat.ENCODING_PCM_8BIT
    PcmEncoding.PCM_16BIT -> AudioFormat.ENCODING_PCM_16BIT
    PcmEncoding.PCM_FLOAT -> AudioFormat.ENCODING_PCM_FLOAT
}

fun PcmSampleRate.asWavSampleRate(): WavSampleRate = WavSampleRate(sampleRate)

fun PcmChannels.asWavChannels(): WavChannels = WavChannels(channelCount)

fun PcmEncoding.asWavBitsPerSample(): WavBitsPerSample = WavBitsPerSample(bitsPerSample)

fun WavBitsPerSample.asPcmEncoding(): PcmEncoding =
    PcmEncoding.values().first { it.bitsPerSample == value }
