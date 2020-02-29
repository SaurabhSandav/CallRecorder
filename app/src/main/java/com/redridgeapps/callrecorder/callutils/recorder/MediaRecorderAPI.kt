package com.redridgeapps.callrecorder.callutils.recorder

import android.media.MediaRecorder
import java.io.File

class MediaRecorderAPI(
    savePath: File
) : Recorder {

    private val fileName = "$savePath/audiorecordtest.mp3"
    private var recorder: MediaRecorder? = null

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
    }

    override fun stopRecording() {
        recorder?.apply {
            stop()
            reset()
            release()
        }
        recorder = null
    }

    override fun releaseRecorder() {
        recorder?.release()
        recorder = null
    }
}
