package com.redridgeapps.callrecorder.callutils

import com.redridgeapps.repository.callutils.AudioRecordChannels
import com.redridgeapps.repository.callutils.AudioRecordEncoding
import com.redridgeapps.repository.callutils.AudioRecordSampleRate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption.CREATE_NEW
import java.nio.file.StandardOpenOption.READ
import java.nio.file.StandardOpenOption.WRITE

object WavFileUtils {

    suspend fun writeHeader(
        fileChannel: FileChannel,
        sampleRate: Int,
        channelCount: Int,
        bitsPerSample: Int,
        size: Int = 0
    ) = withContext(Dispatchers.IO) {

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

        return@withContext
    }

    suspend fun updateHeaderWithSize(fileChannel: FileChannel) = withContext(Dispatchers.IO) {

        val size = fileChannel.size().toInt() - 44
        val byteBuffer = ByteBuffer.allocateDirect(8).order(ByteOrder.LITTLE_ENDIAN)

        byteBuffer.putInt(size + 36)
        byteBuffer.putInt(size)
        byteBuffer.flip()

        byteBuffer.limit(4)
        fileChannel.write(byteBuffer, 4)

        byteBuffer.limit(byteBuffer.capacity())
        fileChannel.write(byteBuffer, 40)

        return@withContext
    }

    suspend fun convertWav8BitTo16Bit(
        inputFilePath: Path,
        outputFilePath: Path
    ) = withContext(Dispatchers.IO) {

        FileChannel.open(outputFilePath, CREATE_NEW, WRITE).use { outputChannel ->

            val inputChannel = FileChannel.open(inputFilePath, READ)
            val input8BitWavData = readWavData(inputChannel)

            if (input8BitWavData.encoding != AudioRecordEncoding.ENCODING_PCM_8BIT) return@withContext

            writeHeader(
                fileChannel = outputChannel,
                sampleRate = input8BitWavData.sampleRate.sampleRate,
                channelCount = input8BitWavData.channels.channelCount,
                bitsPerSample = 16
            )

            inputChannel.position(44)

            val bufferSize = 1024
            val input = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.LITTLE_ENDIAN)
            val output = ByteBuffer.allocateDirect(bufferSize * 2).order(ByteOrder.LITTLE_ENDIAN)

            var read = inputChannel.read(input)

            while (read != -1) {

                input.flip()
                while (input.hasRemaining()) {
                    val short = ((input.get() - 0x80) shl 8).toShort()
                    output.putShort(short)
                }
                input.clear()

                output.flip()
                outputChannel.write(output)
                output.clear()

                read = inputChannel.read(input)
            }

            updateHeaderWithSize(outputChannel)
        }

        return@withContext
    }

    suspend fun readWavData(fileChannel: FileChannel): WavData = withContext(Dispatchers.IO) {

        fileChannel.position(0)

        val byteBuffer = ByteBuffer.allocateDirect(44).order(ByteOrder.LITTLE_ENDIAN)
        fileChannel.read(byteBuffer)

        val fileSize = byteBuffer.getInt(4)
        val channelCount = byteBuffer.getShort(22).toInt()
        val sampleRateInt = byteBuffer.getInt(24)
        val byteRate = byteBuffer.getInt(28)
        val bitsPerSample = byteBuffer.getShort(34).toInt()

        val channels = AudioRecordChannels.values().first { it.channelCount == channelCount }
        val sampleRate = AudioRecordSampleRate.values().first { it.sampleRate == sampleRateInt }
        val encoding = AudioRecordEncoding.values().first { it.bitsPerSample == bitsPerSample }

        return@withContext WavData(
            fileSize = fileSize,
            channels = channels,
            sampleRate = sampleRate,
            encoding = encoding,
            byteRate = byteRate
        )
    }

    data class WavData(
        val fileSize: Int,
        val channels: AudioRecordChannels,
        val sampleRate: AudioRecordSampleRate,
        val encoding: AudioRecordEncoding,
        val byteRate: Int
    ) {
        val bitRate: Float = byteRate * 0.008F
    }
}
