package com.redridgeapps.callrecorder.callutils.recorder

import android.content.SharedPreferences
import android.media.MediaRecorder
import com.redridgeapps.callrecorder.utils.PREF_MEDIA_RECORDER_CHANNELS
import com.redridgeapps.callrecorder.utils.PREF_MEDIA_RECORDER_SAMPLE_RATE
import com.redridgeapps.callrecorder.utils.get
import java.io.File

class MediaRecorderAPI(
    private val saveDir: File,
    private val prefs: SharedPreferences
) : Recorder {

    private val saveFileExt = ".m4a"
    private var recorder: MediaRecorder? = null
    private var savePath: String? = null

    override fun startRecording(fileName: String) {

        val fileNameWithExt = fileName + saveFileExt
        val newSavePath = File(saveDir, fileNameWithExt)
        savePath = newSavePath.absolutePath

        val channels = prefs.get(PREF_MEDIA_RECORDER_CHANNELS)
        val sampleRate = prefs.get(PREF_MEDIA_RECORDER_SAMPLE_RATE)

        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.VOICE_CALL)
            setAudioChannels(channels)
            setAudioSamplingRate(sampleRate)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(newSavePath)
            prepare()
            start()
        }
    }

    override fun stopRecording(): String {
        recorder?.apply {
            stop()
            reset()
            release()
        }
        recorder = null

        return savePath ?: error("savePath is null")
    }

    override fun releaseRecorder() {
        recorder?.release()
        recorder = null
    }
}
