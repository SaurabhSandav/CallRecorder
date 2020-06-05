package com.redridgeapps.wavutils

import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption.*

internal object WavTrimmer {

    fun trimSilenceEnds(
        recordingPath: Path,
        outputPath: Path
    ) {

        FileChannel.open(recordingPath, READ).use { inputChannel ->

            val wavData = WavFileUtils.readWavData(inputChannel)

            val trimmedStartPosition = calculateTrimmedStartPosition(inputChannel, wavData)
            val trimmedEndPosition = calculateTrimmedEndPosition(inputChannel, wavData)

            writeTrimmedFile(
                inputChannel,
                outputPath,
                wavData,
                trimmedStartPosition,
                trimmedEndPosition
            )
        }
    }

    private fun calculateTrimmedStartPosition(
        inputChannel: FileChannel,
        wavData: WavData
    ): Long {

        val sampleSize = wavData.blockAlign
        val buffer = ByteBuffer.allocateDirect(BUFFER_SIZE * sampleSize)
        val addSample = addSampleFactory(wavData, buffer)

        // Skip header
        inputChannel.position(WAV_HEADER_SIZE.toLong())

        while (inputChannel.read(buffer) != -1) {

            buffer.flip()

            loop@ while (buffer.hasRemaining()) {

                when {
                    // Sample is silent, continue loop
                    addSample() == 0 -> continue@loop
                    // Sample is not silent, return previous sample position
                    else -> return inputChannel.position() - buffer.remaining() - sampleSize
                }
            }

            buffer.clear()
        }

        // TODO Handle trimming audio when entirely silent (Hint: Delete recording!)
        return inputChannel.size() // Audio is entirely silent, trim entirely
    }

    private fun calculateTrimmedEndPosition(
        inputChannel: FileChannel,
        wavData: WavData
    ): Long {

        val sampleSize = wavData.blockAlign
        val bufferSize = BUFFER_SIZE * sampleSize
        val buffer = ByteBuffer.allocateDirect(bufferSize)
        val addSample = addSampleFactory(wavData, buffer)

        var position = inputChannel.size()

        while (true) {

            position -= bufferSize

            if (position <= WAV_HEADER_SIZE) break

            inputChannel.read(buffer, position)

            loop@ for (i in buffer.limit() - sampleSize downTo 0 step sampleSize) {

                buffer.position(i)

                when {
                    // Sample is silent, continue loop
                    addSample() == 0 -> continue@loop
                    // Sample is not silent, return previous sample position
                    else -> return position + i + sampleSize
                }
            }

            buffer.clear()
        }

        // TODO Handle trimming audio when entirely silent (Hint: Delete recording!)
        return inputChannel.size() // Audio is entirely silent, trim entirely
    }

    private fun writeTrimmedFile(
        inputChannel: FileChannel,
        outputPath: Path,
        wavData: WavData,
        trimmedStartPosition: Long,
        trimmedEndPosition: Long
    ) {

        FileChannel.open(outputPath, CREATE, TRUNCATE_EXISTING, WRITE).use { outputChannel ->

            // Skip header space in output file for now
            outputChannel.position(44)

            // Copy trimmed audio segment
            inputChannel.transferTo(
                trimmedStartPosition,
                trimmedEndPosition - trimmedStartPosition,
                outputChannel
            )

            // Write header
            WavFileUtils.writeHeader(
                fileChannel = outputChannel,
                bitsPerSample = wavData.bitsPerSample,
                sampleRate = wavData.sampleRate,
                channels = wavData.channels
            )
        }
    }

    private fun addSampleFactory(
        wavData: WavData,
        buffer: ByteBuffer
    ): () -> Int = when (wavData.bitsPerSample.value to wavData.channels.value) {
        8 to 1 -> {
            { buffer.get().toInt() }
        }
        8 to 2 -> {
            { buffer.get().toInt() + buffer.get().toInt() }
        }
        16 to 1 -> {
            { buffer.short.toInt() }
        }
        16 to 2 -> {
            { buffer.short.toInt() + buffer.short.toInt() }
        }
        32 to 1 -> {
            { buffer.int }
        }
        32 to 2 -> {
            { buffer.int + buffer.int }
        }
        else -> error("Invalid channels and/or bitsPerSample")
    }
}

private const val BUFFER_SIZE: Int = 1024