package com.redridgeapps.callutils.recording

import android.media.AudioFormat
import com.redridgeapps.prefs.Prefs
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

fun Prefs.AudioRecord.SampleRate.toPcmSampleRate(): PcmSampleRate = when (this) {
    Prefs.AudioRecord.SampleRate.S44_100 -> PcmSampleRate.S44_100
    Prefs.AudioRecord.SampleRate.S8_000 -> PcmSampleRate.S8_000
    Prefs.AudioRecord.SampleRate.S11_025 -> PcmSampleRate.S11_025
    Prefs.AudioRecord.SampleRate.S16_000 -> PcmSampleRate.S16_000
    Prefs.AudioRecord.SampleRate.S22_050 -> PcmSampleRate.S22_050
    Prefs.AudioRecord.SampleRate.S48_000 -> PcmSampleRate.S48_000
}

fun Prefs.AudioRecord.Channels.toPcmChannels(): PcmChannels = when (this) {
    Prefs.AudioRecord.Channels.MONO -> PcmChannels.MONO
    Prefs.AudioRecord.Channels.STEREO -> PcmChannels.STEREO
}

fun Prefs.AudioRecord.Encoding.toPcmEncoding(): PcmEncoding = when (this) {
    Prefs.AudioRecord.Encoding.PCM_16BIT -> PcmEncoding.PCM_16BIT
    Prefs.AudioRecord.Encoding.PCM_8BIT -> PcmEncoding.PCM_8BIT
    Prefs.AudioRecord.Encoding.PCM_FLOAT -> PcmEncoding.PCM_FLOAT
}
