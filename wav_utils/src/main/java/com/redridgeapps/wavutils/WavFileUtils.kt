package com.redridgeapps.wavutils

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import java.time.Duration

object WavFileUtils {

    fun writeHeader(
        fileChannel: FileChannel,
        sampleRate: WavSampleRate,
        channels: WavChannels,
        bitsPerSample: WavBitsPerSample
    ) {

        val pcmSize = fileChannel.size().toInt() - 44

        fileChannel.position(0)

        val byteBuffer = ByteBuffer.allocateDirect(44).order(ByteOrder.LITTLE_ENDIAN)

        "RIFF".forEach { byteBuffer.put(it.toByte()) }
        byteBuffer.putInt(pcmSize + 36)
        "WAVE".forEach { byteBuffer.put(it.toByte()) }
        "fmt ".forEach { byteBuffer.put(it.toByte()) }
        byteBuffer.putInt(16)

        if (bitsPerSample.value == 32)
            byteBuffer.putShort(3) // WAVE_FORMAT_IEEE_FLOAT
        else
            byteBuffer.putShort(1) // WAVE_FORMAT_PCM

        byteBuffer.putShort(channels.value.toShort())
        byteBuffer.putInt(sampleRate.value)
        byteBuffer.putInt(sampleRate.value * channels.value * bitsPerSample.value / 8)
        byteBuffer.putShort((channels.value * bitsPerSample.value / 8).toShort())
        byteBuffer.putShort(bitsPerSample.value.toShort())
        "data".forEach { byteBuffer.put(it.toByte()) }
        byteBuffer.putInt(pcmSize)

        byteBuffer.flip()

        fileChannel.write(byteBuffer)
    }

    fun readWavData(fileChannel: FileChannel): WavData {

        fileChannel.position(0)

        val byteBuffer = ByteBuffer.allocateDirect(44).order(ByteOrder.LITTLE_ENDIAN)
        fileChannel.read(byteBuffer)

        return WavData(
            fileSize = byteBuffer.getInt(4),
            channels = WavChannels(byteBuffer.getShort(22).toInt()),
            sampleRate = WavSampleRate(byteBuffer.getInt(24)),
            byteRate = byteBuffer.getInt(28),
            blockAlign = byteBuffer.getShort(32).toInt(),
            bitsPerSample = WavBitsPerSample(byteBuffer.getShort(34).toInt())
        )
    }

    fun calculateDuration(
        fileChannel: FileChannel
    ): Duration {
        val wavData = readWavData(fileChannel)
        val fileSize = fileChannel.size() - 44
        val durationMillis = (fileSize * 1000) / wavData.byteRate
        return Duration.ofMillis(durationMillis)
    }
}
