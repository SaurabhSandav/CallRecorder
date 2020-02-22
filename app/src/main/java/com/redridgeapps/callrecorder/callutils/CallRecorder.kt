package com.redridgeapps.callrecorder.callutils

import android.app.Application
import android.media.AudioManager
import android.widget.Toast
import androidx.compose.staticAmbientOf
import androidx.core.content.getSystemService
import androidx.lifecycle.Lifecycle
import com.redridgeapps.callrecorder.callutils.recorder.Recorder

val CallRecorderAmbient = staticAmbientOf<CallRecorder>()

class CallRecorder(
    private val recordingAPI: RecordingAPI,
    private val application: Application,
    private val lifecycle: Lifecycle
) {

    private val savePath = application.externalCacheDir!!.absolutePath
    private val am = application.getSystemService<AudioManager>()!!
    private var currentStreamVolume = -1
    private var recorder: Recorder? = null

    fun startRecording() {

        recorder = recordingAPI.init(savePath, lifecycle)
        recorder!!.startRecording()

        currentStreamVolume = am.getStreamVolume(AudioManager.STREAM_VOICE_CALL)
        val streamMaxVolume = am.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL)
        am.setStreamVolume(AudioManager.STREAM_VOICE_CALL, streamMaxVolume, 0)

        Toast.makeText(application, "Started recording", Toast.LENGTH_SHORT).show()
    }

    fun stopRecording() {

        recorder!!.stopRecording()

        am.setStreamVolume(AudioManager.STREAM_VOICE_CALL, currentStreamVolume, 0)
        currentStreamVolume = -1

        Toast.makeText(application, "Stopped recording", Toast.LENGTH_SHORT).show()
    }
}
