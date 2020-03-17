package com.redridgeapps.callrecorder.callutils.recorder

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import com.redridgeapps.callrecorder.utils.prefs.PREF_AUDIO_RECORD_CHANNEL
import com.redridgeapps.callrecorder.utils.prefs.PREF_AUDIO_RECORD_ENCODING
import com.redridgeapps.callrecorder.utils.prefs.PREF_AUDIO_RECORD_SAMPLE_RATE
import com.redridgeapps.callrecorder.utils.prefs.Prefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import javax.inject.Inject

class AudioRecordAPI @Inject constructor(
    private val prefs: Prefs
) : Recorder {

    private var recorder: AudioRecord? = null
    private var isRecording = false

    override val saveFileExt = "wav"

    override suspend fun startRecording(saveFile: File) = withContext(Dispatchers.IO) {

        val sampleRate = async { prefs.get(PREF_AUDIO_RECORD_SAMPLE_RATE).first() }
        val audioChannel = async { prefs.get(PREF_AUDIO_RECORD_CHANNEL).first() }
        val audioEncoding = async { prefs.get(PREF_AUDIO_RECORD_ENCODING).first() }

        val bufferSize = AudioRecord.getMinBufferSize(
            sampleRate.await(),
            audioChannel.await(),
            audioEncoding.await()
        )

        recorder = AudioRecord(
            MediaRecorder.AudioSource.VOICE_CALL,
            sampleRate.await(),
            audioChannel.await(),
            audioEncoding.await(),
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
        val sampleRate = recorder!!.sampleRate
        val channels = recorder!!.channelCount
        val bitsPerSample = when (recorder!!.audioFormat) {
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
