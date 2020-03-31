package com.redridgeapps.callrecorder.callutils

import android.media.AudioRecord
import com.redridgeapps.repository.callutils.AudioRecordEncoding
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
        val encoding =
            AudioRecordEncoding.values().first { it.encodingFlag == recorder.audioFormat }
        val bitsPerSample = encoding.bitsPerSample

        "RIFF".forEach { byteBuffer.put(it.toByte()) }
        byteBuffer.putInt(0)
        "WAVE".forEach { byteBuffer.put(it.toByte()) }
        "fmt ".forEach { byteBuffer.put(it.toByte()) }
        byteBuffer.putInt(16)

        if (encoding == AudioRecordEncoding.ENCODING_PCM_FLOAT)
            byteBuffer.putShort(3) // WAVE_FORMAT_IEEE_FLOAT
        else
            byteBuffer.putShort(1) // WAVE_FORMAT_PCM

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
