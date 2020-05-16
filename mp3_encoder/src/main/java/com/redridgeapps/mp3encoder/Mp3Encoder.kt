package com.redridgeapps.mp3encoder

import com.redridgeapps.wavutils.WavData
import timber.log.Timber
import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import java.nio.file.StandardOpenOption

object Mp3Encoder {

    fun <T : Buffer> encode(encoder: TypedMP3Encoder<T>) {

        val encodingJob = encoder.encodingJob

        // Open files
        FileChannel.open(encodingJob.wavPath, StandardOpenOption.READ).use { inputChannel ->
            FileChannel.open(
                encodingJob.mp3Path,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
            ).use { outputChannel ->

                // Skip header
                inputChannel.position(44)

                // Setup buffers
                val bufferSize = BUFFER_MULTIPLIER * encodingJob.wavData.blockAlign
                val pcmBuffer =
                    ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.LITTLE_ENDIAN)
                val mp3Buffer =
                    ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.LITTLE_ENDIAN)

                // Init lame
                val lame = initLame(encodingJob.wavData, encodingJob.quality)

                var read: Int
                var write: Int
                val emptyBuffer = ByteBuffer.allocateDirect(0)

                Timber.d("Lame: Encoding started")

                do {
                    read = inputChannel.read(pcmBuffer)
                    pcmBuffer.flip()

                    val numSamples = read / encodingJob.wavData.blockAlign

                    write = when {
                        read < 1 -> Lame.lameEncodeFlush(lame, mp3Buffer, bufferSize)
                        encodingJob.wavData.channels.value == 1 -> encoder.lameEncodeBuffer(
                            lameT = lame,
                            pcmLeft = pcmBuffer,
                            pcmRight = emptyBuffer,
                            numSamples = numSamples,
                            mp3Buffer = mp3Buffer,
                            mp3BufferSize = bufferSize
                        )
                        encodingJob.wavData.channels.value == 2 -> encoder.lameEncodeBufferInterleaved(
                            lameT = lame,
                            pcmBuffer = pcmBuffer,
                            numSamples = numSamples,
                            mp3Buffer = mp3Buffer,
                            mp3BufferSize = bufferSize
                        )
                        else -> error("Only mono and stereo channels supported")
                    }

                    checkEncodingError(write)

                    mp3Buffer.limit(write).rewind()
                    outputChannel.write(mp3Buffer)

                    mp3Buffer.clear()
                    pcmBuffer.clear()
                } while (read > 0)

                Timber.d("Lame: Encoding finished")

                Lame.lameClose(lame)
            }
        }
    }

    private fun initLame(wavData: WavData, quality: Int): Long {

        val lame = Lame.lameInit()
        Lame.lameSetNumChannels(lame, wavData.channels.value)
        Lame.lameSetInSampleRate(lame, wavData.sampleRate.value)
        Lame.lameSetBitRate(lame, wavData.bitRate.toInt())
        Lame.lameSetQuality(lame, quality)
        Lame.lameSetVbr(lame, 0)
        val lameInit = Lame.lameInitParams(lame)

        if (lameInit != 0) error("Lame init unsuccessful")

        return lame
    }

    private fun checkEncodingError(write: Int) {
        when (write) {
            -1 -> error("mp3buf was too small")
            -2 -> error("malloc() problem")
            -3 -> error("lame_init_params() not called")
            -4 -> error("psycho acoustic problems")
        }
    }
}

private const val BUFFER_MULTIPLIER = 4096