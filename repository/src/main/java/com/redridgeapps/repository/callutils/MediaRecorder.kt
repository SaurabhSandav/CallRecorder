package com.redridgeapps.repository.callutils

enum class MediaRecorderChannels(val numChannels: Int) {
    MONO(1),
    STEREO(2)
}

enum class MediaRecorderSampleRate(val sampleRate: Int) {
    S8_000(8_000),
    S11_025(11_025),
    S16_000(16_000),
    S22_050(22_050),
    S44_100(44_100),
    S48_000(48_000)
}
