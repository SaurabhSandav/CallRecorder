package com.redridgeapps.libmp3lame

class Mp3Encoder {

    fun convertWavPcm16ToMP3(
        numChannels: Int,
        sampleRate: Int,
        bitrate: Float,
        quality: Quality,
        wavPath: String,
        mp3Path: String
    ) {
        convertWavPcm16ToMP3(numChannels, sampleRate, bitrate, quality.value, wavPath, mp3Path)
    }

    fun convertWavPcmFloatToMP3(
        numChannels: Int,
        sampleRate: Int,
        bitrate: Float,
        quality: Quality,
        wavPath: String,
        mp3Path: String
    ) {
        convertWavPcmFloatToMP3(numChannels, sampleRate, bitrate, quality.value, wavPath, mp3Path)
    }

    private external fun convertWavPcm16ToMP3(
        numChannels: Int,
        sampleRate: Int,
        bitrate: Float,
        quality: Int,
        wavPath: String,
        mp3Path: String
    )

    private external fun convertWavPcmFloatToMP3(
        numChannels: Int,
        sampleRate: Int,
        bitrate: Float,
        quality: Int,
        wavPath: String,
        mp3Path: String
    )

    companion object {
        init {
            System.loadLibrary("mp3lame")
        }
    }

    enum class Quality(val value: Int) {
        HIGH_SLOW(2),
        MEDIUM_FAST(5),
        LOW_REALLY_FAST(7)
    }
}
