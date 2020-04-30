package com.redridgeapps.mp3encoder

import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

interface TypedMP3Encoder<T : Buffer> {

    val encodingJob: EncodingJob

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

class Pcm8Mp3Encoder(override val encodingJob: EncodingJob) : TypedMP3Encoder<ByteBuffer> {

    private val pcm16Mp3Encoder = run {
        val wavData = encodingJob.wavData

        val newWavData = wavData.copy(
            fileSize = (wavData.fileSize * 2) - 44,
            byteRate = (wavData.channels * wavData.bitsPerSample * wavData.sampleRate) / 8,
            blockAlign = (wavData.channels * wavData.bitsPerSample) / 8,
            bitsPerSample = 16
        )

        Pcm16Mp3Encoder(encodingJob.copy(wavData = newWavData))
    }

    override fun lameEncodeBuffer(
        lameT: Long,
        pcmLeft: ByteBuffer,
        pcmRight: ByteBuffer,
        numSamples: Int,
        mp3Buffer: ByteBuffer,
        mp3BufferSize: Int
    ): Int = pcm16Mp3Encoder.lameEncodeBuffer(
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
    ): Int = pcm16Mp3Encoder.lameEncodeBufferInterleaved(
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

class Pcm16Mp3Encoder(override val encodingJob: EncodingJob) : TypedMP3Encoder<ShortBuffer> {

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

class PcmFloatMp3Encoder(override val encodingJob: EncodingJob) : TypedMP3Encoder<FloatBuffer> {

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