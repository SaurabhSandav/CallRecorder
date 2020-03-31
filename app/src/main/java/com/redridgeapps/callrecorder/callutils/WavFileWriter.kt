package com.redridgeapps.callrecorder.callutils

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

object WavFileWriter {

    fun writeHeader(
        fileChannel: FileChannel,
        sampleRate: Int,
        channelCount: Int,
        bitsPerSample: Int,
        size: Int = 0
    ) {

        fileChannel.position(0)

        val byteBuffer = ByteBuffer.allocateDirect(44).order(ByteOrder.LITTLE_ENDIAN)

        "RIFF".forEach { byteBuffer.put(it.toByte()) }
        byteBuffer.putInt(size + 36)
        "WAVE".forEach { byteBuffer.put(it.toByte()) }
        "fmt ".forEach { byteBuffer.put(it.toByte()) }
        byteBuffer.putInt(16)

        if (bitsPerSample == 32)
            byteBuffer.putShort(3) // WAVE_FORMAT_IEEE_FLOAT
        else
            byteBuffer.putShort(1) // WAVE_FORMAT_PCM

        byteBuffer.putShort(channelCount.toShort())
        byteBuffer.putInt(sampleRate)
        byteBuffer.putInt(sampleRate * channelCount * bitsPerSample / 8)
        byteBuffer.putShort((channelCount * bitsPerSample / 8).toShort())
        byteBuffer.putShort(bitsPerSample.toShort())
        "data".forEach { byteBuffer.put(it.toByte()) }
        byteBuffer.putInt(size)

        byteBuffer.flip()

        fileChannel.write(byteBuffer)
    }

    fun updateHeaderWithSize(fileChannel: FileChannel) {

        val size = fileChannel.size().toInt() - 44
        val byteBuffer = ByteBuffer.allocateDirect(8).order(ByteOrder.LITTLE_ENDIAN)

        byteBuffer.putInt(size + 36)
        byteBuffer.putInt(size)
        byteBuffer.flip()

        byteBuffer.limit(4)
        fileChannel.write(byteBuffer, 4)

        byteBuffer.limit(byteBuffer.capacity())
        fileChannel.write(byteBuffer, 40)
    }
}
