package com.redridgeapps.callrecorder.callutils

import android.content.Context
import android.os.Environment
import androidx.core.content.ContextCompat
import com.redridgeapps.androidwavtomp3.Mp3Encoder
import com.redridgeapps.callrecorder.Recording
import com.redridgeapps.callrecorder.RecordingQueries
import com.redridgeapps.callrecorder.callutils.WavFileUtils.WavData
import com.redridgeapps.callrecorder.utils.extension
import com.redridgeapps.callrecorder.utils.nameWithoutExtension
import com.redridgeapps.repository.callutils.AudioRecordEncoding.ENCODING_PCM_16BIT
import com.redridgeapps.repository.callutils.AudioRecordEncoding.ENCODING_PCM_8BIT
import com.redridgeapps.repository.callutils.AudioRecordEncoding.ENCODING_PCM_FLOAT
import com.redridgeapps.repository.callutils.CallDirection
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Recordings @Inject constructor(
    private val context: Context,
    private val recordingQueries: RecordingQueries,
    private val contactNameFetcher: ContactNameFetcher
) {

    fun generateFilePath(saveFileExt: String): Path {
        val fileName = "${Instant.now().epochSecond}.$saveFileExt"
        return getRecordingStoragePath().resolve(fileName)
    }

    fun saveRecording(
        phoneNumber: String,
        callDirection: CallDirection,
        recordingStartInstant: Instant,
        recordingEndInstant: Instant,
        savePath: Path
    ) {

        val name = contactNameFetcher.getContactName(phoneNumber) ?: "Unknown ($phoneNumber)"

        recordingQueries.insert(
            name = name,
            number = phoneNumber,
            start_instant = recordingStartInstant,
            end_instant = recordingEndInstant,
            call_direction = callDirection,
            save_path = savePath.toAbsolutePath().toString(),
            save_format = savePath.extension
        )
    }

    fun getRecordingList(): Flow<List<Recording>> {
        return recordingQueries.getAll().asFlow().mapToList(Dispatchers.IO)
    }

    suspend fun convertToMp3(recordingId: Int) = withContext(Dispatchers.IO) {

        val recording = recordingQueries.getWithId(recordingId).asFlow().mapToOne().first()
        val recordingPath = Paths.get(recording.save_path)
        val wavData = getWavData(recordingId)
        val outputPathStr =
            "${recordingPath.toAbsolutePath().toString().substringBeforeLast(".")}.mp3"

        val mp3Encoder = Mp3Encoder()

        when (wavData.encoding) {
            ENCODING_PCM_8BIT -> {

                val newInputPath = getTempPath().resolve(
                    "${recordingPath.nameWithoutExtension}.8bit.${recordingPath.extension}"
                )

                WavFileUtils.convertWav8BitTo16Bit(recordingPath, newInputPath)

                val fileChannel = FileChannel.open(newInputPath, StandardOpenOption.READ)
                val newWavData = WavFileUtils.readWavData(fileChannel)
                fileChannel.close()

                mp3Encoder.convertWavPcm16ToMP3(
                    channelCount = newWavData.channels.channelCount,
                    sampleRate = newWavData.sampleRate.sampleRate,
                    bitrate = newWavData.bitRate,
                    quality = Mp3Encoder.Quality.HIGH_SLOW,
                    wavPath = newInputPath.toAbsolutePath().toString(),
                    mp3Path = outputPathStr
                )

                Files.delete(newInputPath)
            }
            ENCODING_PCM_16BIT -> mp3Encoder.convertWavPcm16ToMP3(
                channelCount = wavData.channels.channelCount,
                sampleRate = wavData.sampleRate.sampleRate,
                bitrate = wavData.bitRate,
                quality = Mp3Encoder.Quality.HIGH_SLOW,
                wavPath = recordingPath.toAbsolutePath().toString(),
                mp3Path = outputPathStr
            )
            ENCODING_PCM_FLOAT -> mp3Encoder.convertWavPcmFloatToMP3(
                channelCount = wavData.channels.channelCount,
                sampleRate = wavData.sampleRate.sampleRate,
                bitrate = wavData.bitRate,
                quality = Mp3Encoder.Quality.HIGH_SLOW,
                wavPath = recordingPath.toAbsolutePath().toString(),
                mp3Path = outputPathStr
            )
        }

        return@withContext
    }

    suspend fun deleteRecording(recordingId: Int) = withContext(Dispatchers.IO) {
        val recording = recordingQueries.getWithId(recordingId).asFlow().mapToOne().first()
        Files.delete(Paths.get(recording.save_path))
        recordingQueries.deleteWithId(recordingId)
    }

    private fun getRecordingStoragePath(): Path {

        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED)
            error("External storage is not writable")

        val externalStorageVolumes = ContextCompat.getExternalFilesDirs(context, null)
        val primaryExternalStorage = externalStorageVolumes[0]
        val savePath = Paths.get(primaryExternalStorage.path, "CallRecordings")

        if (!Files.exists(savePath))
            Files.createDirectory(savePath)

        return savePath
    }

    private fun getTempPath(): Path {
        val tempPath = getRecordingStoragePath().resolve("Tmp")

        if (!Files.exists(tempPath))
            Files.createDirectory(tempPath)

        return tempPath
    }

    private suspend fun getWavData(recordingId: Int): WavData = withContext(Dispatchers.IO) {
        val recording = recordingQueries.getWithId(recordingId).asFlow().mapToOne().first()
        val recordingPath = Paths.get(recording.save_path)
        val fileChannel = FileChannel.open(recordingPath, StandardOpenOption.READ)

        fileChannel.use {
            return@withContext WavFileUtils.readWavData(fileChannel)
        }
    }
}
