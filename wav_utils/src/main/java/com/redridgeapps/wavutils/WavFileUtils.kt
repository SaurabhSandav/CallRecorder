package com.redridgeapps.wavutils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

object WavFileUtils {

    suspend fun writeHeader(
        fileChannel: FileChannel,
        sampleRate: Int,
        channelCount: Int,
        bitsPerSample: Int
    ) = withContext(Dispatchers.IO) {

        val pcmSize = fileChannel.size().toInt() - 44

        fileChannel.position(0)

        val byteBuffer = ByteBuffer.allocateDirect(44).order(ByteOrder.LITTLE_ENDIAN)

        "RIFF".forEach { byteBuffer.put(it.toByte()) }
        byteBuffer.putInt(pcmSize + 36)
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
        byteBuffer.putInt(pcmSize)

        byteBuffer.flip()

        fileChannel.write(byteBuffer)

        return@withContext
    }

    suspend fun readWavData(fileChannel: FileChannel): WavData = withContext(Dispatchers.IO) {

        fileChannel.position(0)

        val byteBuffer = ByteBuffer.allocateDirect(44).order(ByteOrder.LITTLE_ENDIAN)
        fileChannel.read(byteBuffer)

        return@withContext WavData(
            fileSize = byteBuffer.getInt(4),
            channels = byteBuffer.getShort(22).toInt(),
            sampleRate = byteBuffer.getInt(24),
            byteRate = byteBuffer.getInt(28),
            blockAlign = byteBuffer.getShort(32).toInt(),
            bitsPerSample = byteBuffer.getShort(34).toInt()
        )
    }
}
