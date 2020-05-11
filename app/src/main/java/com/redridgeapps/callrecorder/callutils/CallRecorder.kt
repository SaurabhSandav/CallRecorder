package com.redridgeapps.callrecorder.callutils

import android.media.AudioRecord
import android.media.MediaRecorder.AudioSource
import android.os.PowerManager
import com.redridgeapps.callrecorder.utils.ToastMaker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
    private var audioWriter: AudioWriter? = null

    suspend fun startRecording(
        recordingJob: RecordingJob,
        audioWriter: AudioWriter
    ) = withContext(Dispatchers.IO) {

        if (this@CallRecorder.recordingJob != null)
            error("Previous recording resources not cleaned up")

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

        this@CallRecorder.audioWriter = audioWriter
        audioWriter.startWriting(recorder!!, recordingJob, bufferSize)

        return@withContext
    }

    suspend fun stopRecording() {

        recorder ?: return

        recorder!!.stop()

        audioWriter!!.awaitWritingFinished()

        recorder!!.release()
        recorder = null

        releaseWakeLock()
        toastMaker.newToast("Stopped recording").show()

        recordings.saveRecording(recordingJob!!)

        recordingJob = null
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
