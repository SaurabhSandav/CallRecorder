package com.redridgeapps.callrecorder.callutils

import android.media.AudioManager
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import com.redridgeapps.callrecorder.callutils.recorder.AudioRecordAPI
import com.redridgeapps.callrecorder.callutils.recorder.MediaRecorderAPI
import com.redridgeapps.callrecorder.callutils.recorder.Recorder
import com.redridgeapps.callrecorder.utils.ToastMaker
import com.redridgeapps.callrecorder.utils.prefs.PREF_RECORDING_API
import com.redridgeapps.callrecorder.utils.prefs.Prefs
import com.redridgeapps.repository.callutils.RecordingAPI
import kotlinx.coroutines.flow.first
import java.io.File
import java.time.Instant
import javax.inject.Inject
import javax.inject.Provider

class CallRecorder @Inject constructor(
    private val audioManager: AudioManager,
    private val powerManager: PowerManager,
    private val prefs: Prefs,
    private val toastMaker: ToastMaker,
    private val recordings: Recordings,
    private val audioRecordAPI: Provider<AudioRecordAPI>,
    private val mediaRecorderAPI: Provider<MediaRecorderAPI>
) {

    private var recorder: Recorder? = null
    private var saveFile: File? = null
    private var wakeLock: WakeLock? = null
    private var currentStreamVolume = -1
    private var recordingStartTime: Long = -1
    private var recordingEndTime: Long = -1

    suspend fun startRecording() {

        val recordingAPIStr = prefs.get(PREF_RECORDING_API).first()
        val recordingAPI = RecordingAPI.valueOf(recordingAPIStr)

        recorder = when (recordingAPI) {
            RecordingAPI.MediaRecorder -> mediaRecorderAPI.get()
            RecordingAPI.AudioRecord -> audioRecordAPI.get()
        }
        saveFile = recordings.generateFileName(recorder!!.saveFileExt)
        recordingStartTime = Instant.now().toEpochMilli()

        recorder!!.startRecording(saveFile!!)

        maximizeVolume()
        acquireWakeLock()

        toastMaker.newToast("Started recording").show()
    }

    fun stopRecording() {

        recorder!!.stopRecording()

        recorder = null
        recordingEndTime = Instant.now().toEpochMilli()

        releaseWakeLock()
        restoreVolume()

        toastMaker.newToast("Stopped recording").show()
        recordings.insertRecording(recordingStartTime, recordingEndTime, saveFile!!)
    }

    fun releaseRecorder() {
        recorder?.releaseRecorder()
        recorder = null

        releaseWakeLock()
    }

    private fun acquireWakeLock() {
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyApp::MyWakelockTag")
        //noinspection WakelockTimeout
        wakeLock!!.acquire()
    }

    private fun releaseWakeLock() {
        wakeLock?.release()
        wakeLock = null
    }

    private fun maximizeVolume() {
        currentStreamVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL)
        val streamMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL)
        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, streamMaxVolume, 0)
    }

    private fun restoreVolume() {
        audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, currentStreamVolume, 0)
        currentStreamVolume = -1
    }
}
