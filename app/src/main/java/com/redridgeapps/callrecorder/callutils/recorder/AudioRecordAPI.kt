package com.redridgeapps.callrecorder.callutils.recorder

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import java.io.BufferedOutputStream
import java.io.DataOutputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class AudioRecordAPI(
    savePath: String,
    private val lifecycle: Lifecycle
) : Recorder {

    private val filePath = "$savePath/voice.pcm"
    private var recorder: AudioRecord? = null
    private var recordingThread: Thread? = null
    private var isRecording = false

    private val observer = object : DefaultLifecycleObserver {
        override fun onStop(owner: LifecycleOwner) {
            releaseRecorder()
        }
    }

    override fun startRecording() {

        val bufferSize = AudioRecord.getMinBufferSize(
            RECORDER_SAMPLE_RATE,
            RECORDER_CHANNELS,
            RECORDER_AUDIO_ENCODING
        )

        recorder = AudioRecord(
            MediaRecorder.AudioSource.VOICE_CALL,
            RECORDER_SAMPLE_RATE,
            RECORDER_CHANNELS,
            RECORDER_AUDIO_ENCODING,
            bufferSize
        )

        recorder!!.startRecording()
        isRecording = true
        recordingThread =
            Thread(Runnable { writeAudioDataToFile(bufferSize) }, "AudioRecorder Thread")
        recordingThread!!.start()

        lifecycle.addObserver(observer)
    }

    override fun stopRecording() {
        if (recorder != null) {
            isRecording = false
            recorder!!.stop()
            recorder!!.release()
            recorder = null
            recordingThread = null
        }

        lifecycle.removeObserver(observer)
    }

    override fun releaseRecorder() {
        recorder?.release()
        recorder = null

        lifecycle.removeObserver(observer)
    }

    private fun writeAudioDataToFile(bufferSize: Int) {

        val shortBuffer = ShortArray(bufferSize / 2)
        val dos = DataOutputStream(BufferedOutputStream(FileOutputStream(filePath)))

        while (isRecording) {
            recorder!!.read(shortBuffer, 0, shortBuffer.size)

            val byteBuffer = shortBuffer.to2ByteArray()
            dos.write(byteBuffer)
        }

        dos.close()
    }

    private fun ShortArray.to2ByteArray(): ByteArray {

        val resultBytes = ByteArray(size * 2)
        val byteBuffer = ByteBuffer.wrap(resultBytes).order(ByteOrder.LITTLE_ENDIAN)

        map { byteBuffer.putShort(it) }

        return resultBytes
    }
}

private const val RECORDER_SAMPLE_RATE = 44_100
private const val RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO
private const val RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT
