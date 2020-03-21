package com.redridgeapps.repository.callutils

import android.media.AudioFormat

enum class AudioRecordSampleRate(val sampleRate: Int) {
    S44_100(44_100),
    S48_000(48_000);
}

enum class AudioRecordChannels(val channels: Int) {
    MONO(AudioFormat.CHANNEL_IN_MONO),
    STEREO(AudioFormat.CHANNEL_IN_STEREO)

}

enum class AudioRecordEncoding(val encoding: Int) {
    ENCODING_PCM_8BIT(AudioFormat.ENCODING_PCM_8BIT),
    ENCODING_PCM_16BIT(AudioFormat.ENCODING_PCM_16BIT),
    ENCODING_PCM_FLOAT(AudioFormat.ENCODING_PCM_FLOAT)
}
