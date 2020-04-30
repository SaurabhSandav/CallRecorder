package com.redridgeapps.mp3encoder

import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

interface TypedMP3Encoder<T : Buffer> {

    fun lameEncodeBuffer(
        lameT: Long,
        pcmLeft: ByteBuffer,
        pcmRight: ByteBuffer,
        numSamples: Int,
        mp3Buffer: ByteBuffer,
        mp3BufferSize: Int
    ): Int

    fun lameEncodeBufferInterleaved(
        lameT: Long,
        pcmBuffer: ByteBuffer,
        numSamples: Int,
        mp3Buffer: ByteBuffer,
        mp3BufferSize: Int
    ): Int
}

object Pcm8Mp3Encoder : TypedMP3Encoder<ByteBuffer> {

    override fun lameEncodeBuffer(
        lameT: Long,
        pcmLeft: ByteBuffer,
        pcmRight: ByteBuffer,
        numSamples: Int,
        mp3Buffer: ByteBuffer,
        mp3BufferSize: Int
    ): Int = Pcm16Mp3Encoder.lameEncodeBuffer(
        lameT = lameT,
        pcmLeft = pcmLeft.convert8BitTo16Bit(),
        pcmRight = pcmRight.convert8BitTo16Bit(),
        numSamples = numSamples,
        mp3Buffer = mp3Buffer,
        mp3BufferSize = mp3BufferSize
    )

    override fun lameEncodeBufferInterleaved(
        lameT: Long,
        pcmBuffer: ByteBuffer,
        numSamples: Int,
        mp3Buffer: ByteBuffer,
        mp3BufferSize: Int
    ): Int = Pcm16Mp3Encoder.lameEncodeBufferInterleaved(
        lameT = lameT,
        pcmBuffer = pcmBuffer.convert8BitTo16Bit(),
        numSamples = numSamples,
        mp3Buffer = mp3Buffer,
        mp3BufferSize = mp3BufferSize
    )

    private fun ByteBuffer.convert8BitTo16Bit(): ByteBuffer {

        val outputBuffer =
            ByteBuffer.allocateDirect(capacity() * 2).order(ByteOrder.LITTLE_ENDIAN)

        while (hasRemaining()) {
            val short = ((get() - 0x80) shl 8).toShort()
            outputBuffer.putShort(short)
        }

        outputBuffer.flip()

        return outputBuffer
    }
}

object Pcm16Mp3Encoder : TypedMP3Encoder<ShortBuffer> {

    override fun lameEncodeBuffer(
        lameT: Long,
        pcmLeft: ByteBuffer,
        pcmRight: ByteBuffer,
        numSamples: Int,
        mp3Buffer: ByteBuffer,
        mp3BufferSize: Int
    ): Int = Lame.lameEncodeBuffer(
        lameT = lameT,
        pcmLeft = pcmLeft.asShortBuffer(),
        pcmRight = pcmRight.asShortBuffer(),
        numSamples = numSamples,
        mp3Buffer = mp3Buffer,
        mp3BufferSize = mp3BufferSize
    )

    override fun lameEncodeBufferInterleaved(
        lameT: Long,
        pcmBuffer: ByteBuffer,
        numSamples: Int,
        mp3Buffer: ByteBuffer,
        mp3BufferSize: Int
    ): Int = Lame.lameEncodeBufferInterleaved(
        lameT = lameT,
        pcmBuffer = pcmBuffer.asShortBuffer(),
        numSamples = numSamples,
        mp3Buffer = mp3Buffer,
        mp3BufferSize = mp3BufferSize
    )
}

object PcmFloatMp3Encoder : TypedMP3Encoder<FloatBuffer> {

    override fun lameEncodeBuffer(
        lameT: Long,
        pcmLeft: ByteBuffer,
        pcmRight: ByteBuffer,
        numSamples: Int,
        mp3Buffer: ByteBuffer,
        mp3BufferSize: Int
    ): Int = Lame.lameEncodeBufferIeeeFloat(
        lameT = lameT,
        pcmLeft = pcmLeft.asFloatBuffer(),
        pcmRight = pcmRight.asFloatBuffer(),
        numSamples = numSamples,
        mp3Buffer = mp3Buffer,
        mp3BufferSize = mp3BufferSize
    )

    override fun lameEncodeBufferInterleaved(
        lameT: Long,
        pcmBuffer: ByteBuffer,
        numSamples: Int,
        mp3Buffer: ByteBuffer,
        mp3BufferSize: Int
    ): Int = Lame.lameEncodeBufferInterleavedIeeeFloat(
        lameT = lameT,
        pcmBuffer = pcmBuffer.asFloatBuffer(),
        numSamples = numSamples,
        mp3Buffer = mp3Buffer,
        mp3BufferSize = mp3BufferSize
    )
}