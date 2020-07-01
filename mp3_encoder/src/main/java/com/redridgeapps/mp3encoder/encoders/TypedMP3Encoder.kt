package com.redridgeapps.mp3encoder.encoders

import com.redridgeapps.mp3encoder.EncodingJob
import java.nio.Buffer
import java.nio.ByteBuffer

internal interface TypedMP3Encoder<T : Buffer> {

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
