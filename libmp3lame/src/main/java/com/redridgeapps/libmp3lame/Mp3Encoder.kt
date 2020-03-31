package com.redridgeapps.libmp3lame

class Mp3Encoder {

    external fun convertWavPcm16ToMP3(
        numChannels: Int,
        sampleRate: Int,
        bitrate: Float,
        quality: Int,
        wavPath: String,
        mp3Path: String
    )

    external fun convertWavPcmFloatToMP3(
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
}
