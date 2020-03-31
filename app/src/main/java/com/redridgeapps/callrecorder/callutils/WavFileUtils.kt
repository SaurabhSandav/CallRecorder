package com.redridgeapps.callrecorder.callutils

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption.*

object WavFileUtils {

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

    fun convertWav8BitTo16Bit(inputFilePath: Path, outputFilePath: Path) {

        FileChannel.open(outputFilePath, CREATE, WRITE).use { outputChannel ->

            val inputChannel = FileChannel.open(inputFilePath, READ)
            val input8BitWavData = readWavData(inputChannel)

            if (input8BitWavData.bitsPerSample != 8) return

            writeHeader(
                fileChannel = outputChannel,
                sampleRate = input8BitWavData.sampleRate,
                channelCount = input8BitWavData.channelCount,
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
    }

    fun readWavData(fileChannel: FileChannel): WavData {

        fileChannel.position(0)

        val byteBuffer = ByteBuffer.allocate(44).order(ByteOrder.LITTLE_ENDIAN)
        fileChannel.read(byteBuffer)

        return WavData(
            fileSize = byteBuffer.getInt(4),
            channelCount = byteBuffer.getShort(22).toInt(),
            sampleRate = byteBuffer.getInt(24),
            byteRate = byteBuffer.getInt(28),
            bitsPerSample = byteBuffer.getInt(34)
        )
    }

    data class WavData(
        val fileSize: Int,
        val channelCount: Int,
        val sampleRate: Int,
        val byteRate: Int,
        val bitsPerSample: Int
    )
}
