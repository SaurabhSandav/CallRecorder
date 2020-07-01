package com.redridgeapps.mp3encoder.encoders

import com.redridgeapps.mp3encoder.EncodingJob
import com.redridgeapps.mp3encoder.lame.Lame
import java.nio.ByteBuffer
import java.nio.ShortBuffer

internal class Pcm16Mp3Encoder(
    override val encodingJob: EncodingJob
) : TypedMP3Encoder<ShortBuffer> {

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
