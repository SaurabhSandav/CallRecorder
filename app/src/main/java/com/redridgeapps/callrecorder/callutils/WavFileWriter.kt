package com.redridgeapps.callrecorder.callutils

import android.media.AudioFormat
import android.media.AudioRecord
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class WavFileWriter(
    private val recorder: AudioRecord,
    private val fileChannel: FileChannel
) {

    fun writeHeader() {

        val byteBuffer = ByteBuffer.allocateDirect(44).order(ByteOrder.LITTLE_ENDIAN)
        val sampleRate = recorder.sampleRate
        val channels = recorder.channelCount
        val bitsPerSample = when (recorder.audioFormat) {
            AudioFormat.ENCODING_PCM_FLOAT -> 32
            AudioFormat.ENCODING_PCM_16BIT -> 16
            AudioFormat.ENCODING_PCM_8BIT -> 8
            else -> error("Invalid audio encoding")
        }

        "RIFF".forEach { byteBuffer.put(it.toByte()) }
        byteBuffer.putInt(0)
        "WAVE".forEach { byteBuffer.put(it.toByte()) }
        "fmt ".forEach { byteBuffer.put(it.toByte()) }
        byteBuffer.putInt(16)
        byteBuffer.putShort(1)
        byteBuffer.putShort(channels.toShort())
        byteBuffer.putInt(sampleRate)
        byteBuffer.putInt(sampleRate * channels * bitsPerSample / 8)
        byteBuffer.putShort((channels * bitsPerSample / 8).toShort())
        byteBuffer.putShort(bitsPerSample.toShort())
        "data".forEach { byteBuffer.put(it.toByte()) }
        byteBuffer.putInt(0)

        byteBuffer.flip()

        fileChannel.write(byteBuffer)
    }

    fun updateHeaderWithSize() {

        val size = fileChannel.size().toInt() - 44
        val byteBuffer = ByteBuffer.allocateDirect(4).order(ByteOrder.LITTLE_ENDIAN)

        byteBuffer.putInt(size + 36)
        byteBuffer.flip()

        fileChannel.position(4)
        fileChannel.write(byteBuffer)

        byteBuffer.clear()
        byteBuffer.putInt(size)
        byteBuffer.flip()

        fileChannel.position(40)
        fileChannel.write(byteBuffer)
    }
}
