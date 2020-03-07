package com.redridgeapps.callrecorder.callutils.recorder

import android.content.SharedPreferences
import android.media.AudioRecord
import android.media.MediaRecorder
import com.redridgeapps.callrecorder.utils.PREF_AUDIO_RECORD_AUDIO_ENCODING
import com.redridgeapps.callrecorder.utils.PREF_AUDIO_RECORD_CHANNELS
import com.redridgeapps.callrecorder.utils.PREF_AUDIO_RECORD_SAMPLE_RATE
import com.redridgeapps.callrecorder.utils.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedOutputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class AudioRecordAPI(
    private val prefs: SharedPreferences
) : Recorder {

    private var recorder: AudioRecord? = null
    private var isRecording = false

    override val saveFileExt = "pcm"

    override suspend fun startRecording(saveFile: File) = withContext(Dispatchers.IO) {

        val sampleRate = prefs.get(PREF_AUDIO_RECORD_SAMPLE_RATE)
        val channels = prefs.get(PREF_AUDIO_RECORD_CHANNELS)
        val audioEncoding = prefs.get(PREF_AUDIO_RECORD_AUDIO_ENCODING)

        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channels, audioEncoding)

        recorder = AudioRecord(
            MediaRecorder.AudioSource.VOICE_CALL,
            sampleRate,
            channels,
            audioEncoding,
            bufferSize
        )

        recorder!!.startRecording()
        isRecording = true

        writeAudioDataToFile(saveFile, bufferSize)
    }

    override fun stopRecording() {

        isRecording = false

        recorder?.apply {
            stop()
            release()
        }

        recorder = null
    }

    override fun releaseRecorder() {
        recorder?.release()
        recorder = null
    }

    private suspend fun writeAudioDataToFile(
        saveFile: File,
        bufferSize: Int
    ) = withContext(Dispatchers.IO) {

        DataOutputStream(BufferedOutputStream(FileOutputStream(saveFile))).use { dos ->
            val shortBuffer = ShortArray(bufferSize / 2)

            while (isRecording) {
                recorder!!.read(shortBuffer, 0, shortBuffer.size, AudioRecord.READ_BLOCKING)

                val byteBuffer = shortBuffer.to2ByteArray()
                dos.write(byteBuffer)
            }
        }
    }

    private fun ShortArray.to2ByteArray(): ByteArray {

        val resultBytes = ByteArray(size * 2)
        val byteBuffer = ByteBuffer.wrap(resultBytes).order(ByteOrder.LITTLE_ENDIAN)

        map { byteBuffer.putShort(it) }

        return resultBytes
    }
}
