package com.redridgeapps.callrecorder.callutils

import android.media.AudioRecord
import android.media.MediaRecorder.AudioSource
import android.os.PowerManager
import com.redridgeapps.callrecorder.utils.ToastMaker
import com.redridgeapps.wavutils.WavFileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption.CREATE_NEW
import java.nio.file.StandardOpenOption.WRITE
import javax.inject.Inject

class CallRecorder @Inject constructor(
    powerManager: PowerManager,
    private val toastMaker: ToastMaker,
    private val recordings: Recordings
) {

    private val wakeLock = powerManager.newWakeLock(
        PowerManager.PARTIAL_WAKE_LOCK,
        javaClass.simpleName
    )

    private var recorder: AudioRecord? = null
    private var recordingJob: RecordingJob? = null
    private var isRecording = false

    suspend fun startRecording(recordingJob: RecordingJob) = withContext(Dispatchers.IO) {

        this@CallRecorder.recordingJob = recordingJob

        withContext(Dispatchers.Main) {
            toastMaker.newToast("Started recording").show()
            acquireWakeLock()
        }

        val sampleRate = recordingJob.pcmSampleRate.sampleRate
        val channel = recordingJob.pcmChannels.toAudioRecordChannel()
        val encoding = recordingJob.pcmEncoding.toAudioRecordEncoding()

        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channel, encoding) + BUFFER_ADDER

        recorder = AudioRecord(AudioSource.VOICE_CALL, sampleRate, channel, encoding, bufferSize)
        recorder!!.startRecording()

        isRecording = true

        writeAudioDataToWavFile(bufferSize)

        return@withContext
    }

    fun stopRecording() {

        recorder ?: return

        recorder!!.stop()
        recorder!!.release()

        isRecording = false

        recorder = null
        recordingJob = null

        releaseWakeLock()
        toastMaker.newToast("Stopped recording").show()

        recordings.saveRecording(
            recordingJob!!.phoneNumber,
            recordingJob!!.callDirection,
            recordingJob!!.savePath
        )
    }

    private suspend fun writeAudioDataToWavFile(bufferSize: Int) = withContext(Dispatchers.IO) {

        FileChannel.open(recordingJob!!.savePath, CREATE_NEW, WRITE).use { channel ->

            // Skip header for now
            channel.position(44)

            val byteBuffer = ByteBuffer.allocateDirect(bufferSize)

            while (isRecording) {
                byteBuffer.clear()

                val bytesRead = recorder!!.read(
                    byteBuffer,
                    byteBuffer.capacity(),
                    AudioRecord.READ_BLOCKING
                )

                crashIfError(bytesRead, recordingJob!!.savePath)

                byteBuffer.position(bytesRead)
                byteBuffer.flip()

                channel.write(byteBuffer)
            }

            WavFileUtils.writeHeader(
                fileChannel = channel,
                sampleRate = recordingJob!!.pcmSampleRate.sampleRate,
                channelCount = recordingJob!!.pcmChannels.channelCount,
                bitsPerSample = recordingJob!!.pcmEncoding.bitsPerSample
            )
        }

        return@withContext
    }

    private suspend fun crashIfError(bytesRead: Int, savePath: Path) = withContext(Dispatchers.IO) {

        val errorStr = when (bytesRead) {
            AudioRecord.ERROR_INVALID_OPERATION -> "AudioRecord: ERROR_INVALID_OPERATION"
            AudioRecord.ERROR_BAD_VALUE -> "AudioRecord: ERROR_BAD_VALUE"
            AudioRecord.ERROR_DEAD_OBJECT -> "AudioRecord: ERROR_DEAD_OBJECT"
            else -> return@withContext
        }

        withContext(Dispatchers.Main) {
            toastMaker.newToast("Call recording stopped with error")
            Timber.d(errorStr)
        }

        Files.delete(savePath)

        error(errorStr)
    }

    private fun acquireWakeLock() {
        //noinspection WakelockTimeout
        wakeLock.acquire()
    }

    private fun releaseWakeLock() {
        if (wakeLock.isHeld)
            wakeLock.release()
    }
}

private const val BUFFER_ADDER = 4096
