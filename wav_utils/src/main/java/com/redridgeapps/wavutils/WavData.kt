package com.redridgeapps.wavutils

data class WavData(
    val fileSize: Int,
    val channels: Int,
    val sampleRate: Int,
    val byteRate: Int,
    val blockAlign: Int,
    val bitsPerSample: Int
) {
    val bitRate: Float = byteRate * 0.008F
}