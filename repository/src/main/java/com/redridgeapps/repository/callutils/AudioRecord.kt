package com.redridgeapps.repository.callutils

enum class AudioRecordChannels(val numChannels: Int) {
    MONO(1),
    STEREO(2);

    companion object {

        fun valueOf(numChannels: Int): AudioRecordChannels {
            return values().firstOrNull { it.numChannels == numChannels }
                ?: error("Invalid num of channels")
        }
    }
}

enum class AudioRecordSampleRate(val sampleRate: Int) {
    S44_100(44_100),
    S48_000(48_000);

    companion object {

        fun valueOf(sampleRate: Int): AudioRecordSampleRate {
            return values().firstOrNull { it.sampleRate == sampleRate }
                ?: error("Invalid sample rate")
        }
    }
}

enum class AudioRecordEncoding {
    ENCODING_PCM_8BIT,
    ENCODING_PCM_16BIT,
    ENCODING_PCM_FLOAT;
}
