package com.redridgeapps.wavutils

data class WavData(
    val fileSize: Int,
    val channels: WavChannels,
    val sampleRate: WavSampleRate,
    val byteRate: Int,
    val blockAlign: Int,
    val bitsPerSample: WavBitsPerSample
) {
    val bitRate: Float = byteRate * 0.008F
}

inline class WavChannels(val value: Int)

inline class WavSampleRate(val value: Int)

inline class WavBitsPerSample(val value: Int)
