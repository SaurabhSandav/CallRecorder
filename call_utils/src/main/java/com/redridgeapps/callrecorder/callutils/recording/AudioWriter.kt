package com.redridgeapps.callrecorder.callutils.recording

import android.media.AudioRecord
import com.redridgeapps.wavutils.WAV_HEADER_SIZE
import com.redridgeapps.wavutils.WavFileUtils
import kotlinx.coroutines.*
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption.CREATE_NEW
import java.nio.file.StandardOpenOption.WRITE

class AudioWriter(private val coroutineScope: CoroutineScope) {

    private val writingFinishedSignal = CompletableDeferred(Unit)

    suspend fun awaitWritingFinished() = writingFinishedSignal.await()

    suspend fun startWriting(
        recorder: AudioRecord,
        recordingJob: RecordingJob,
        bufferSize: Int
    ) = coroutineScope.launch(Dispatchers.IO) {

        FileChannel.open(recordingJob.savePath, CREATE_NEW, WRITE).use { channel ->

            // Skip header for now
            channel.position(WAV_HEADER_SIZE.toLong())

            val byteBuffer = ByteBuffer.allocateDirect(bufferSize)

            while (recorder.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
                byteBuffer.clear()

                val bytesRead = recorder.read(
                    byteBuffer,
                    byteBuffer.capacity(),
                    AudioRecord.READ_BLOCKING
                )

                checkError(bytesRead, recordingJob.savePath)

                byteBuffer.position(bytesRead)
                byteBuffer.flip()

                channel.write(byteBuffer)
            }

            WavFileUtils.writeHeader(
                fileChannel = channel,
                sampleRate = recordingJob.pcmSampleRate.asWavSampleRate(),
                channels = recordingJob.pcmChannels.asWavChannels(),
                bitsPerSample = recordingJob.pcmEncoding.asWavBitsPerSample()
            )
        }

        writingFinishedSignal.complete(Unit)
    }

    private suspend fun checkError(bytesRead: Int, savePath: Path) = withContext(Dispatchers.IO) {

        val errorStr = when (bytesRead) {
            AudioRecord.ERROR_INVALID_OPERATION -> "AudioRecord: ERROR_INVALID_OPERATION"
            AudioRecord.ERROR_BAD_VALUE -> "AudioRecord: ERROR_BAD_VALUE"
            AudioRecord.ERROR_DEAD_OBJECT -> "AudioRecord: ERROR_DEAD_OBJECT"
            else -> return@withContext
        }

        Files.delete(savePath)
        error(errorStr)
    }
}
