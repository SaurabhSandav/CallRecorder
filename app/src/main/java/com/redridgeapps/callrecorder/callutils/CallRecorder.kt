package com.redridgeapps.callrecorder.callutils

import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.PowerManager
import com.redridgeapps.callrecorder.utils.ToastMaker
import com.redridgeapps.callrecorder.utils.prefs.PREF_AUDIO_RECORD_CHANNELS
import com.redridgeapps.callrecorder.utils.prefs.PREF_AUDIO_RECORD_ENCODING
import com.redridgeapps.callrecorder.utils.prefs.PREF_AUDIO_RECORD_SAMPLE_RATE
import com.redridgeapps.callrecorder.utils.prefs.Prefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
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
    private var saveFile: File? = null
    private var recordingStartInstant: Instant = Instant.MIN
    private var recordingEndInstant: Instant = Instant.MIN

    suspend fun startRecording() {

        toastMaker.newToast("Started recording").show()
        acquireWakeLock()

        withContext(Dispatchers.IO) {

            saveFile = recordings.generateFileName(saveFileExt)
            recordingStartInstant = Instant.now()

            val sampleRate = async { prefs.get(PREF_AUDIO_RECORD_SAMPLE_RATE).sampleRate }
            val audioChannel = async { prefs.get(PREF_AUDIO_RECORD_CHANNELS).channels }
            val audioEncoding = async { prefs.get(PREF_AUDIO_RECORD_ENCODING).encoding }

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

            writeAudioDataToWavFile(saveFile!!, bufferSize)
        }
    }

    fun stopRecording(phoneNumber: String, callType: String) {

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
            callType,
            recordingStartInstant,
            recordingEndInstant,
            saveFile!!
        )
    }

    fun releaseRecorder() {
        recorder?.release()
        recorder = null

        releaseWakeLock()
    }

    private suspend fun writeAudioDataToWavFile(saveFile: File, bufferSize: Int) {
        withContext(Dispatchers.IO) {

            FileOutputStream(saveFile).channel.use { channel ->

                val wavFileWriter = WavFileWriter(recorder!!, channel)

                wavFileWriter.writeHeader()

                val byteBuffer = ByteBuffer.allocateDirect(bufferSize)

                while (isRecording) {
                    byteBuffer.clear()

                    val bytesRead = recorder!!.read(
                        byteBuffer,
                        byteBuffer.capacity(),
                        AudioRecord.READ_BLOCKING
                    )

                    crashIfError(saveFile, bytesRead)

                    byteBuffer.position(bytesRead)
                    byteBuffer.flip()

                    channel.write(byteBuffer)
                }

                wavFileWriter.updateHeaderWithSize()
            }
        }
    }

    private suspend fun crashIfError(saveFile: File, bytesRead: Int) {
        withContext(Dispatchers.Main) {

            val errorStr: String? = when (bytesRead) {
                AudioRecord.ERROR_INVALID_OPERATION -> "AudioRecord: ERROR_INVALID_OPERATION"
                AudioRecord.ERROR_BAD_VALUE -> "AudioRecord: ERROR_BAD_VALUE"
                AudioRecord.ERROR_DEAD_OBJECT -> "AudioRecord: ERROR_DEAD_OBJECT"
                else -> null
            }

            errorStr?.let {

                toastMaker.newToast("Call recording stopped with error")
                Timber.d(it)

                saveFile.delete()

                error(it)
            }
        }
    }

    private fun acquireWakeLock() {
        //noinspection WakelockTimeout
        wakeLock.acquire()
    }

    private fun releaseWakeLock() {
        wakeLock.release()
    }
}
