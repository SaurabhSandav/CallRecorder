package com.redridgeapps.callrecorder.callutils

import android.content.Context
import android.os.Environment
import androidx.core.content.ContextCompat
import com.redridgeapps.callrecorder.Recording
import com.redridgeapps.callrecorder.RecordingQueries
import com.redridgeapps.callrecorder.utils.extension
import com.redridgeapps.callrecorder.utils.nameWithoutExtension
import com.redridgeapps.mp3encoding.EncodingJob
import com.redridgeapps.mp3encoding.Mp3Encoder
import com.redridgeapps.repository.callutils.CallDirection
import com.redridgeapps.repository.callutils.PcmEncoding
import com.redridgeapps.repository.callutils.PcmEncoding.PCM_16BIT
import com.redridgeapps.repository.callutils.PcmEncoding.PCM_8BIT
import com.redridgeapps.repository.callutils.PcmEncoding.PCM_FLOAT
import com.redridgeapps.repository.callutils.WavData
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
        val outputPath =
            recordingPath.parent.resolve("${recordingPath.fileName.nameWithoutExtension}.mp3")

        val mp3Encoder = Mp3Encoder()

        val encodingJob = EncodingJob(
            wavData = wavData,
            wavPath = recordingPath,
            mp3Path = outputPath
        )

        when (PcmEncoding.valueOf(wavData.bitsPerSample)) {
            PCM_8BIT -> {
                val newWavData = wavData.copy(
                    fileSize = (wavData.fileSize * 2) - 44,
                    byteRate = (wavData.channels * wavData.bitsPerSample * wavData.sampleRate) / 8,
                    blockAlign = (wavData.channels * wavData.bitsPerSample) / 8,
                    bitsPerSample = 16
                )
                mp3Encoder.convertWavPcm8ToMP3(encodingJob.copy(wavData = newWavData))
            }
            PCM_16BIT -> mp3Encoder.convertWavPcm16ToMP3(encodingJob)
            PCM_FLOAT -> mp3Encoder.convertWavPcmFloatToMP3(encodingJob)
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

    private suspend fun getWavData(recordingId: Int): WavData = withContext(Dispatchers.IO) {
        val recording = recordingQueries.getWithId(recordingId).asFlow().mapToOne().first()
        val recordingPath = Paths.get(recording.save_path)
        val fileChannel = FileChannel.open(recordingPath, StandardOpenOption.READ)

        fileChannel.use {
            return@withContext WavFileUtils.readWavData(fileChannel)
        }
    }
}
