package com.redridgeapps.callrecorder.callutils

import android.app.Application
import android.media.AudioManager
import android.media.MediaRecorder
import android.widget.Toast
import androidx.compose.staticAmbientOf
import androidx.core.content.getSystemService
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

val CallRecorderAmbient = staticAmbientOf<CallRecorder>()

class CallRecorder(
    private val application: Application,
    private val lifecycle: Lifecycle
) : DefaultLifecycleObserver {

    private val fileName = "${application.externalCacheDir!!.absolutePath}/audiorecordtest.m4a"
    private val am = application.getSystemService<AudioManager>()!!
    private var recorder: MediaRecorder? = null
    private var currentStreamVolume = -1

    fun startRecording() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setAudioChannels(2)
            setAudioSamplingRate(44_100)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(fileName)
            prepare()
            start()
        }

        lifecycle.addObserver(this)

        currentStreamVolume = am.getStreamVolume(AudioManager.STREAM_VOICE_CALL)
        val streamMaxVolume = am.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL)
        am.setStreamVolume(AudioManager.STREAM_VOICE_CALL, streamMaxVolume, 0)

        Toast.makeText(application, "Started recording", Toast.LENGTH_SHORT).show()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        recorder?.release()
        recorder = null

        lifecycle.removeObserver(this)
    }

    fun stopRecording() {
        recorder?.apply {
            stop()
            reset()
            release()
        }
        recorder = null

        am.setStreamVolume(AudioManager.STREAM_VOICE_CALL, currentStreamVolume, 0)
        currentStreamVolume = -1

        lifecycle.removeObserver(this)

        Toast.makeText(application, "Stopped recording", Toast.LENGTH_SHORT).show()
    }
}
