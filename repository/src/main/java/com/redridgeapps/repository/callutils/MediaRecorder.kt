package com.redridgeapps.repository.callutils

enum class MediaRecorderChannels(val numChannels: Int) {
    MONO(1),
    STEREO(2)
}

enum class MediaRecorderSampleRate(val sampleRate: Int) {
    S44_100(44_100),
    S48_000(48_000)
}
