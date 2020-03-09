package com.redridgeapps.callrecorder.callutils.recorder

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import com.redridgeapps.callrecorder.utils.PREF_AUDIO_RECORD_CHANNEL
import com.redridgeapps.callrecorder.utils.PREF_AUDIO_RECORD_ENCODING
import com.redridgeapps.callrecorder.utils.PREF_AUDIO_RECORD_SAMPLE_RATE
import com.redridgeapps.callrecorder.utils.Prefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class AudioRecordAPI(
    prefs: Prefs
) : Recorder {

    private var recorder: AudioRecord? = null
    private var isRecording = false
    private val sampleRate = prefs.get(PREF_AUDIO_RECORD_SAMPLE_RATE)
    private val audioChannel = prefs.get(PREF_AUDIO_RECORD_CHANNEL)
    private val audioEncoding = prefs.get(PREF_AUDIO_RECORD_ENCODING)

    override val saveFileExt = "wav"

    override suspend fun startRecording(saveFile: File) = withContext(Dispatchers.IO) {

        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, audioChannel, audioEncoding)

        recorder = AudioRecord(
            MediaRecorder.AudioSource.VOICE_CALL,
            sampleRate,
            audioChannel,
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

    private fun writeAudioDataToFile(saveFile: File, bufferSize: Int) {

        FileOutputStream(saveFile).channel.use { channel ->

            writeHeader(channel)

            val byteBuffer = ByteBuffer.allocateDirect(bufferSize)

            while (isRecording) {
                byteBuffer.clear()

                val bytesRead = recorder!!.read(
                    byteBuffer,
                    byteBuffer.capacity(),
                    AudioRecord.READ_BLOCKING
                )

                byteBuffer.position(bytesRead)
                byteBuffer.flip()

                channel.write(byteBuffer)
            }

            updateHeaderWithSize(channel)
        }
    }

    private fun writeHeader(channel: FileChannel) {

        val byteBuffer = ByteBuffer.allocateDirect(44).order(ByteOrder.LITTLE_ENDIAN)
        val channels = when (audioChannel) {
            AudioFormat.CHANNEL_IN_MONO -> 1
            AudioFormat.CHANNEL_IN_STEREO -> 2
            else -> error("Invalid audio channel")
        }
        val bitsPerSample = when (audioEncoding) {
            AudioFormat.ENCODING_PCM_16BIT -> 16
            AudioFormat.ENCODING_PCM_8BIT -> 8
            else -> error("Invalid audio encoding")
        }

        "RIFF".forEach { byteBuffer.put(it.toByte()) }
        byteBuffer.putInt(0)
        "WAVE".forEach { byteBuffer.put(it.toByte()) }
        "fmt ".forEach { byteBuffer.put(it.toByte()) }
        byteBuffer.putInt(16)
        byteBuffer.putShort(1)
        byteBuffer.putShort(channels.toShort())
        byteBuffer.putInt(sampleRate)
        byteBuffer.putInt(sampleRate * channels * bitsPerSample / 8)
        byteBuffer.putShort((channels * bitsPerSample / 8).toShort())
        byteBuffer.putShort(bitsPerSample.toShort())
        "data".forEach { byteBuffer.put(it.toByte()) }
        byteBuffer.putInt(0)

        byteBuffer.flip()
        channel.write(byteBuffer)
    }

    private fun updateHeaderWithSize(channel: FileChannel) {

        val size = channel.size().toInt() - 44
        val byteBuffer = ByteBuffer.allocateDirect(4).order(ByteOrder.LITTLE_ENDIAN)

        byteBuffer.putInt(size + 36)
        byteBuffer.flip()

        channel.position(4)
        channel.write(byteBuffer)

        byteBuffer.clear()
        byteBuffer.putInt(size)
        byteBuffer.flip()

        channel.position(40)
        channel.write(byteBuffer)
    }
}
