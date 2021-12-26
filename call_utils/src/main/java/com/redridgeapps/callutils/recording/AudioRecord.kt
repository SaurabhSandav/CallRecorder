package com.redridgeapps.callutils.recording

object AudioRecord {

    enum class SampleRate(val value: Int) {
        S44_100(44_100),
        S8_000(8_000),
        S11_025(11_025),
        S16_000(16_000),
        S22_050(22_050),
        S48_000(48_000),
    }

    enum class Channels(val value: Int) {
        MONO(1),
        STEREO(2),
    }

    enum class Encoding(val value: Int) {
        PCM_8BIT(8),
        PCM_16BIT(16),
        PCM_FLOAT(32),
    }
}
