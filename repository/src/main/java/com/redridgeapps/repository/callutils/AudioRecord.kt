package com.redridgeapps.repository.callutils

import android.media.AudioFormat

enum class AudioRecordSampleRate(val sampleRate: Int) {
    S8_000(8_000),
    S11_025(11_025),
    S16_000(16_000),
    S22_050(22_050),
    S44_100(44_100),
    S48_000(48_000);
}

enum class AudioRecordChannels(val channels: Int) {
    MONO(AudioFormat.CHANNEL_IN_MONO),
    STEREO(AudioFormat.CHANNEL_IN_STEREO)
}

enum class AudioRecordEncoding(val encoding: Int, val bitsPerSample: Int) {
    ENCODING_PCM_8BIT(AudioFormat.ENCODING_PCM_8BIT, 8),
    ENCODING_PCM_16BIT(AudioFormat.ENCODING_PCM_16BIT, 16),
    ENCODING_PCM_FLOAT(AudioFormat.ENCODING_PCM_FLOAT, 32)
}
