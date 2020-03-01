package com.redridgeapps.callrecorder.callutils.recorder

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import java.io.BufferedOutputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.time.Instant

class AudioRecordAPI(
    private val saveDir: File
) : Recorder {

    private val saveFileExt = ".pcm"
    private var recorder: AudioRecord? = null
    private var recordingThread: Thread? = null
    private var isRecording = false

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
    }

    override fun stopRecording() {
        if (recorder != null) {
            isRecording = false
            recorder!!.stop()
            recorder!!.release()
            recorder = null
            recordingThread = null
        }
    }

    override fun releaseRecorder() {
        recorder?.release()
        recorder = null
    }

    private fun writeAudioDataToFile(bufferSize: Int) {

        val fileName = Instant.now().toEpochMilli().toString() + saveFileExt
        val savePath = File(saveDir, fileName)

        val dos = DataOutputStream(BufferedOutputStream(FileOutputStream(savePath)))
        val shortBuffer = ShortArray(bufferSize / 2)

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
