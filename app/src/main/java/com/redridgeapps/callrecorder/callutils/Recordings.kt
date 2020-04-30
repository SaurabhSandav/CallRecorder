package com.redridgeapps.callrecorder.callutils

import android.content.Context
import android.os.Environment
import androidx.core.content.ContextCompat
import com.redridgeapps.callrecorder.Recording
import com.redridgeapps.callrecorder.RecordingQueries
import com.redridgeapps.callrecorder.callutils.PcmEncoding.PCM_16BIT
import com.redridgeapps.callrecorder.callutils.PcmEncoding.PCM_8BIT
import com.redridgeapps.callrecorder.callutils.PcmEncoding.PCM_FLOAT
import com.redridgeapps.callrecorder.utils.extension
import com.redridgeapps.callrecorder.utils.nameWithoutExtension
import com.redridgeapps.mp3encoder.EncodingJob
import com.redridgeapps.mp3encoder.Mp3Encoder
import com.redridgeapps.mp3encoder.Pcm16Mp3Encoder
import com.redridgeapps.mp3encoder.Pcm8Mp3Encoder
import com.redridgeapps.mp3encoder.PcmFloatMp3Encoder
import com.redridgeapps.wavutils.WavData
import com.redridgeapps.wavutils.WavFileUtils
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

        val encodingJob = EncodingJob(
            wavData = wavData,
            wavPath = recordingPath,
            mp3Path = outputPath
        )

        val encoder = when (PcmEncoding.valueOf(wavData.bitsPerSample)) {
            PCM_8BIT -> Pcm8Mp3Encoder(encodingJob)
            PCM_16BIT -> Pcm16Mp3Encoder(encodingJob)
            PCM_FLOAT -> PcmFloatMp3Encoder(encodingJob)
        }

        Mp3Encoder.encode(encoder)

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

        return@withContext fileChannel.use { WavFileUtils.readWavData(fileChannel) }
    }
}
