package com.redridgeapps.callrecorder.callutils.recorder

import android.media.MediaRecorder
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

class MediaRecorderAPI(
    savePath: String,
    private val lifecycle: Lifecycle
) : Recorder {

    private val fileName = "$savePath/audiorecordtest.mp3"
    private var recorder: MediaRecorder? = null

    private val observer = object : DefaultLifecycleObserver {
        override fun onStop(owner: LifecycleOwner) {
            recorder?.release()
            recorder = null

            lifecycle.removeObserver(this)
        }
    }

    override fun startRecording() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.VOICE_CALL)
            setAudioChannels(1)
            setAudioSamplingRate(44_100)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(fileName)
            prepare()
            start()
        }

        lifecycle.addObserver(observer)
    }

    override fun stopRecording() {
        recorder?.apply {
            stop()
            reset()
            release()
        }
        recorder = null

        lifecycle.removeObserver(observer)
    }
}
