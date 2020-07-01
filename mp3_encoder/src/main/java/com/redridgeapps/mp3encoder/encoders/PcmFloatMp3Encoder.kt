package com.redridgeapps.mp3encoder.encoders

import com.redridgeapps.mp3encoder.EncodingJob
import com.redridgeapps.mp3encoder.Lame
import java.nio.ByteBuffer
import java.nio.FloatBuffer

internal class PcmFloatMp3Encoder(
    override val encodingJob: EncodingJob
) : TypedMP3Encoder<FloatBuffer> {

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
