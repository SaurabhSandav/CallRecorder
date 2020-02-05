package com.redridgeapps.callrecorder.callutils

import android.media.MediaRecorder
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Ambient
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

val CallRecorderAmbient = Ambient.of<CallRecorder>()

class CallRecorder(private val activity: AppCompatActivity) : DefaultLifecycleObserver {

    private val fileName = "${activity.externalCacheDir!!.absolutePath}/audiorecordtest.3gp"
    private var recorder: MediaRecorder? = null

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        recorder?.release()
        recorder = null

        activity.lifecycle.removeObserver(this)
    }

    fun startRecording() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(fileName)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            prepare()
            start()
        }

        activity.lifecycle.addObserver(this)
    }

    fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        recorder = null

        activity.lifecycle.removeObserver(this)
    }
}
