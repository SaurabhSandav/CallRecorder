package com.redridgeapps.callrecorder.callutils.recorder

import android.media.MediaRecorder
import com.redridgeapps.callrecorder.utils.prefs.PREF_MEDIA_RECORDER_CHANNELS
import com.redridgeapps.callrecorder.utils.prefs.PREF_MEDIA_RECORDER_SAMPLE_RATE
import com.redridgeapps.callrecorder.utils.prefs.Prefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File

class MediaRecorderAPI(
    private val prefs: Prefs
) : Recorder {

    private var recorder: MediaRecorder? = null

    override val saveFileExt = "m4a"

    override suspend fun startRecording(saveFile: File) = withContext(Dispatchers.IO) {

        val channels = prefs.get(PREF_MEDIA_RECORDER_CHANNELS).first()
        val sampleRate = prefs.get(PREF_MEDIA_RECORDER_SAMPLE_RATE).first()

        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.VOICE_CALL)
            setAudioChannels(channels)
            setAudioSamplingRate(sampleRate)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(saveFile)
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
