package com.redridgeapps.callrecorder.callutils.recorder

import android.media.MediaRecorder
import java.io.File
import java.time.Instant

class MediaRecorderAPI(
    private val saveDir: File
) : Recorder {

    private val saveFileExt = ".mp3"
    private var recorder: MediaRecorder? = null

    override fun startRecording() {

        val fileName = Instant.now().toEpochMilli().toString() + saveFileExt
        val savePath = File(saveDir, fileName)

        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.VOICE_CALL)
            setAudioChannels(1)
            setAudioSamplingRate(44_100)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(savePath)
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
