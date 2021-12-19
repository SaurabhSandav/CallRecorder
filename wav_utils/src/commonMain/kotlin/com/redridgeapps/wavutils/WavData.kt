package com.redridgeapps.wavutils

public data class WavData(
    val fileSize: Int,
    val channels: WavChannels,
    val sampleRate: WavSampleRate,
    val byteRate: Int,
    val blockAlign: Int,
    val bitsPerSample: WavBitsPerSample,
) {
    val bitRate: Float = byteRate * 0.008F
}

public inline class WavChannels(public val value: Int)

public inline class WavSampleRate(public val value: Int)

public inline class WavBitsPerSample(public val value: Int)
