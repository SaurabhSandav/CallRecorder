package com.redridgeapps.mp3encoding

import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.ShortBuffer

object Lame {

    init {
        System.loadLibrary("lame_wrapper")
    }

    @JvmStatic
    external fun getLameVersion(): String

    @JvmStatic
    external fun lameInit(): Long

    @JvmStatic
    external fun lameClose(lameT: Long): Long

    @JvmStatic
    external fun lameSetNumChannels(lameT: Long, numChannels: Int): Int

    @JvmStatic
    external fun lameSetInSampleRate(lameT: Long, sampleRate: Int): Int

    @JvmStatic
    external fun lameSetBitRate(lameT: Long, bitRate: Int): Int

    @JvmStatic
    external fun lameSetQuality(lameT: Long, quality: Int): Int

    @JvmStatic
    external fun lameSetVbr(lameT: Long, vbrMode: Int): Int

    @JvmStatic
    external fun lameInitParams(lameT: Long): Int

    @JvmStatic
    external fun lameEncodeBuffer(
        lameT: Long,
        pcmLeft: ShortBuffer,
        pcmRight: ShortBuffer,
        numSamples: Int,
        mp3Buffer: ByteBuffer,
        mp3BufferSize: Int
    ): Int

    @JvmStatic
    external fun lameEncodeBufferInterleaved(
        lameT: Long,
        pcmBuffer: ShortBuffer,
        numSamples: Int,
        mp3Buffer: ByteBuffer,
        mp3BufferSize: Int
    ): Int

    external fun lameEncodeBufferIeeeFloat(
        lameT: Long,
        pcmLeft: FloatBuffer,
        pcmRight: FloatBuffer,
        numSamples: Int,
        mp3Buffer: ByteBuffer,
        mp3BufferSize: Int
    ): Int

    external fun lameEncodeBufferInterleavedIeeeFloat(
        lameT: Long,
        pcmBuffer: FloatBuffer,
        numSamples: Int,
        mp3Buffer: ByteBuffer,
        mp3BufferSize: Int
    ): Int

    external fun lameEncodeFlush(
        lameT: Long,
        mp3Buffer: ByteBuffer,
        size: Int
    ): Int
}