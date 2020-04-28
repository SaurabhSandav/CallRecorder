package com.redridgeapps.callrecorder.callutils

import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.PowerManager
import com.redridgeapps.callrecorder.utils.ToastMaker
import com.redridgeapps.callrecorder.utils.prefs.PREF_AUDIO_RECORD_CHANNELS
import com.redridgeapps.callrecorder.utils.prefs.PREF_AUDIO_RECORD_ENCODING
import com.redridgeapps.callrecorder.utils.prefs.PREF_AUDIO_RECORD_SAMPLE_RATE
import com.redridgeapps.callrecorder.utils.prefs.Prefs
import com.redridgeapps.repository.callutils.CallDirection
import com.redridgeapps.repository.callutils.PcmEncoding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardOpenOption.CREATE_NEW
import java.nio.file.StandardOpenOption.WRITE
import java.time.Instant
import javax.inject.Inject

class CallRecorder @Inject constructor(
    powerManager: PowerManager,
    private val prefs: Prefs,
    private val toastMaker: ToastMaker,
    private val recordings: Recordings
) {

    private val saveFileExt = "wav"
    private val wakeLock = powerManager.newWakeLock(
        PowerManager.PARTIAL_WAKE_LOCK,
        javaClass.simpleName
    )

    private var recorder: AudioRecord? = null
    private var isRecording = false
    private var savePath: Path? = null
    private var recordingStartInstant: Instant = Instant.MIN
    private var recordingEndInstant: Instant = Instant.MIN

    suspend fun startRecording() = withContext(Dispatchers.IO) {

        withContext(Dispatchers.Main) {
            toastMaker.newToast("Started recording").show()
            acquireWakeLock()
        }

        savePath = recordings.generateFilePath(saveFileExt)
        recordingStartInstant = Instant.now()

        val sampleRate = async { prefs.get(PREF_AUDIO_RECORD_SAMPLE_RATE).sampleRate }
        val audioChannel = async { prefs.get(PREF_AUDIO_RECORD_CHANNELS).toAudioRecordChannels() }
        val audioEncoding = async { prefs.get(PREF_AUDIO_RECORD_ENCODING).toAudioRecordEncoding() }

        val bufferSize = AudioRecord.getMinBufferSize(
            sampleRate.await(),
            audioChannel.await(),
            audioEncoding.await()
        )

        recorder = AudioRecord(
            MediaRecorder.AudioSource.VOICE_CALL,
            sampleRate.await(),
            audioChannel.await(),
            audioEncoding.await(),
            bufferSize
        )

        recorder!!.startRecording()
        isRecording = true

        writeAudioDataToWavFile(bufferSize)

        return@withContext
    }

    fun stopRecording(phoneNumber: String, callDirection: CallDirection) {

        isRecording = false

        recorder?.apply {
            stop()
            release()
        }

        recorder = null
        recordingEndInstant = Instant.now()

        releaseWakeLock()
        toastMaker.newToast("Stopped recording").show()

        recordings.saveRecording(
            phoneNumber,
            callDirection,
            recordingStartInstant,
            recordingEndInstant,
            savePath!!
        )
    }

    fun releaseRecorder() {
        recorder?.release()
        recorder = null

        releaseWakeLock()
    }

    private suspend fun writeAudioDataToWavFile(bufferSize: Int) = withContext(Dispatchers.IO) {

        FileChannel.open(savePath, CREATE_NEW, WRITE).use { channel ->

            val encoding =
                PcmEncoding.values().first { it.toAudioRecordEncoding() == recorder!!.audioFormat }
            val bitsPerSample = encoding.bitsPerSample

            WavFileUtils.writeHeader(
                fileChannel = channel,
                sampleRate = recorder!!.sampleRate,
                channelCount = recorder!!.channelCount,
                bitsPerSample = bitsPerSample
            )

            val byteBuffer = ByteBuffer.allocateDirect(bufferSize)

            while (isRecording) {
                byteBuffer.clear()

                val bytesRead = recorder!!.read(
                    byteBuffer,
                    byteBuffer.capacity(),
                    AudioRecord.READ_BLOCKING
                )

                crashIfError(bytesRead)

                byteBuffer.position(bytesRead)
                byteBuffer.flip()

                channel.write(byteBuffer)
            }

            WavFileUtils.updateHeaderWithSize(channel)
        }

        return@withContext
    }

    private suspend fun crashIfError(bytesRead: Int) = withContext(Dispatchers.IO) {

        val errorStr: String? = when (bytesRead) {
            AudioRecord.ERROR_INVALID_OPERATION -> "AudioRecord: ERROR_INVALID_OPERATION"
            AudioRecord.ERROR_BAD_VALUE -> "AudioRecord: ERROR_BAD_VALUE"
            AudioRecord.ERROR_DEAD_OBJECT -> "AudioRecord: ERROR_DEAD_OBJECT"
            else -> null
        }

        errorStr?.let {

            withContext(Dispatchers.Main) {
                toastMaker.newToast("Call recording stopped with error")
                Timber.d(it)
            }

            Files.delete(savePath)

            error(it)
        }

        return@withContext
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
