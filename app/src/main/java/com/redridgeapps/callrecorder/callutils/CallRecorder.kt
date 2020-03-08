package com.redridgeapps.callrecorder.callutils

import android.content.SharedPreferences
import android.media.AudioManager
import com.redridgeapps.callrecorder.callutils.recorder.Recorder
import com.redridgeapps.callrecorder.utils.PREF_RECORDING_API
import com.redridgeapps.callrecorder.utils.ToastMaker
import com.redridgeapps.callrecorder.utils.get
import java.io.File
import java.time.Instant
import javax.inject.Inject

class CallRecorder @Inject constructor(
    private val am: AudioManager,
    private val prefs: SharedPreferences,
    private val toastMaker: ToastMaker,
    private val recordings: Recordings
) {

    private var recorder: Recorder? = null
    private var saveFile: File? = null
    private var currentStreamVolume = -1
    private var recordingStartTime: Long = -1
    private var recordingEndTime: Long = -1

    suspend fun startRecording() {

        val recordingAPIStr = prefs.get(PREF_RECORDING_API)
        val recordingAPI = RecordingAPI.valueOf(recordingAPIStr)

        recorder = recordingAPI.init(prefs)
        saveFile = recordings.generateFileName(recorder!!.saveFileExt)
        recordingStartTime = Instant.now().toEpochMilli()

        recorder!!.startRecording(saveFile!!)

        maximizeVolume()
        toastMaker.newToast("Started recording").show()
    }

    fun stopRecording() {

        recorder!!.stopRecording()
        recorder = null

        recordingEndTime = Instant.now().toEpochMilli()
        restoreVolume()
        toastMaker.newToast("Stopped recording").show()
        recordings.insertRecording(recordingStartTime, recordingEndTime, saveFile!!)
    }

    fun releaseRecorder() {
        recorder?.releaseRecorder()
        recorder = null
    }

    private fun maximizeVolume() {
        currentStreamVolume = am.getStreamVolume(AudioManager.STREAM_VOICE_CALL)
        val streamMaxVolume = am.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL)
        am.setStreamVolume(AudioManager.STREAM_VOICE_CALL, streamMaxVolume, 0)
    }

    private fun restoreVolume() {
        am.setStreamVolume(AudioManager.STREAM_VOICE_CALL, currentStreamVolume, 0)
        currentStreamVolume = -1
    }
}
