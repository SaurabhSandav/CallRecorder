package com.redridgeapps.callrecorder.callutils.recorder

import android.content.SharedPreferences
import android.media.MediaRecorder
import com.redridgeapps.callrecorder.utils.PREF_MEDIA_RECORDER_CHANNELS
import com.redridgeapps.callrecorder.utils.PREF_MEDIA_RECORDER_SAMPLE_RATE
import com.redridgeapps.callrecorder.utils.get
import java.io.File
import java.time.Instant

class MediaRecorderAPI(
    private val saveDir: File,
    private val prefs: SharedPreferences
) : Recorder {

    private val saveFileExt = ".mp3"
    private var recorder: MediaRecorder? = null

    override fun startRecording() {

        val fileName = Instant.now().toEpochMilli().toString() + saveFileExt
        val savePath = File(saveDir, fileName)

        val channels = prefs.get(PREF_MEDIA_RECORDER_CHANNELS)
        val sampleRate = prefs.get(PREF_MEDIA_RECORDER_SAMPLE_RATE)

        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.VOICE_CALL)
            setAudioChannels(channels)
            setAudioSamplingRate(sampleRate)
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
