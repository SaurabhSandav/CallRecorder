package com.redridgeapps.repository.callutils

enum class MediaRecorderChannel(val numChannels: Int) {
    MONO(1),
    STEREO(2);

    companion object {

        fun valueOf(numChannels: Int): MediaRecorderChannel {
            return values().firstOrNull { it.numChannels == numChannels }
                ?: error("Invalid num of channels")
        }
    }
}

enum class MediaRecorderSampleRate(val sampleRate: Int) {
    S44_100(44_100),
    S48_000(48_000);

    companion object {

        fun valueOf(sampleRate: Int): MediaRecorderSampleRate {
            return values().firstOrNull { it.sampleRate == sampleRate }
                ?: error("Invalid sample rate")
        }
    }
}
