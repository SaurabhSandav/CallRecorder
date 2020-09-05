package com.redridgeapps.callrecorder.callutils.storage

import android.content.Context
import android.os.Environment
import androidx.core.content.ContextCompat
import com.redridgeapps.callrecorder.callutils.db.Recording
import com.redridgeapps.callrecorder.callutils.db.RecordingId
import com.redridgeapps.callrecorder.callutils.db.RecordingQueries
import com.redridgeapps.callrecorder.callutils.recording.RecordingJob
import com.redridgeapps.callrecorder.common.AppDispatchers
import com.redridgeapps.callrecorder.common.StartupInitializer
import com.redridgeapps.callrecorder.common.utils.extension
import com.redridgeapps.callrecorder.common.utils.launchUnit
import com.redridgeapps.callrecorder.common.utils.replaceExtension
import com.redridgeapps.callrecorder.prefs.PREF_RECORDINGS_STORAGE_PATH
import com.redridgeapps.callrecorder.prefs.Prefs
import com.redridgeapps.mp3encoder.EncodingJob
import com.redridgeapps.mp3encoder.Mp3Encoder
import com.redridgeapps.wavutils.WavData
import com.redridgeapps.wavutils.WavFileUtils
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption.READ
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration

@Singleton
class Recordings @Inject internal constructor(
    private val recordingQueries: RecordingQueries,
    private val contactNameFetcher: ContactNameFetcher,
    private val dispatchers: AppDispatchers,
) {

    suspend fun saveRecording(recordingJob: RecordingJob) = withContext(dispatchers.IO) {

        val phoneNumber = recordingJob.newCallEvent.phoneNumber
        val name = contactNameFetcher.getContactName(phoneNumber) ?: "Unknown ($phoneNumber)"

        val duration = FileChannel.open(recordingJob.savePath.toAbsolutePath(), READ)
            .use { WavFileUtils.calculateDuration(it) }

        recordingQueries.insert(
            name = name,
            number = phoneNumber,
            call_instant = recordingJob.recordingStartInstant,
            call_duration = duration,
            call_direction = recordingJob.newCallEvent.callDirection,
            save_path = recordingJob.savePath.toAbsolutePath().toString(),
            save_format = recordingJob.savePath.extension
        )
    }

    fun getRecording(recordingId: RecordingId): Flow<Recording> {
        return recordingQueries.get(listOf(recordingId)).asFlow().mapToOne(dispatchers.IO)
    }

    fun getRecordings(recordingIdList: List<RecordingId>): Flow<List<Recording>> {
        return recordingQueries.get(recordingIdList).asFlow().mapToList(dispatchers.IO)
    }

    fun getAllRecordings(): Flow<List<Recording>> {
        return recordingQueries.getAll().asFlow().mapToList(dispatchers.IO)
    }

    suspend fun trimSilenceEnds(recordingId: RecordingId) = withContext(dispatchers.IO) {

        val recording = getRecording(recordingId).first()
        val recordingPath = Paths.get(recording.save_path)
        val outputPath = recordingPath.replaceExtension("trimmed.wav")

        WavFileUtils.trimSilenceEnds(recordingPath, outputPath)

        // Replace original file with trimmed file
        Files.delete(recordingPath)
        Files.move(outputPath, recordingPath)

        // Update duration
        val duration =
            FileChannel.open(recordingPath, READ).use { WavFileUtils.calculateDuration(it) }
        recordingQueries.updateDuration(duration, recordingId)
    }

    suspend fun convertToMp3(recordingId: RecordingId) = withContext(dispatchers.IO) {

        val recording = getRecording(recordingId).first()
        val recordingPath = Paths.get(recording.save_path)
        val wavData = getWavData(recordingId)
        val outputPath = recordingPath.replaceExtension("mp3")

        val encodingJob = EncodingJob(
            wavData = wavData,
            wavPath = recordingPath,
            mp3Path = outputPath
        )

        Mp3Encoder.encode(encodingJob)
    }

    suspend fun deleteRecording(recordingIdList: List<RecordingId>) = withContext(dispatchers.IO) {
        val recordings = recordingQueries.get(recordingIdList).executeAsList()

        recordings.forEach {
            Files.delete(Paths.get(it.save_path))
        }

        recordingQueries.delete(recordingIdList)
    }

    suspend fun setIsStarred(
        newValue: Boolean,
        recordingIdList: List<RecordingId>,
    ) = withContext(dispatchers.IO) {
        recordingQueries.setIsStarred(newValue, recordingIdList)
    }

    suspend fun setSkipAutoDelete(
        newValue: Boolean,
        recordingIdList: List<RecordingId>,
    ) = withContext(dispatchers.IO) {
        recordingQueries.setSkipAutoDelete(newValue, recordingIdList)
    }

    suspend fun updateContactNames() = withContext(dispatchers.IO) {

        val recordings = recordingQueries.getNameAndNumbers().executeAsList()

        recordings.forEach { recording ->
            val name = contactNameFetcher.getContactName(recording.number) ?: recording.name
            recordingQueries.updateContactName(name, recording.number)
        }
    }

    suspend fun getWavData(recordingId: RecordingId): WavData = withContext(dispatchers.IO) {

        val recording = getRecording(recordingId).first()
        val recordingPath = Paths.get(recording.save_path)
        val fileChannel = FileChannel.open(recordingPath, READ)

        return@withContext fileChannel.use { WavFileUtils.readWavData(fileChannel) }
    }

    internal suspend fun deleteAutoIfOlderThan(duration: Duration) = withContext(dispatchers.IO) {
        val days = duration.inDays.toInt()
        recordingQueries.deleteAutoIfOlderThan(days.toString())
    }

    companion object {

        internal fun generateFilePath(saveDir: String, fileName: String): Path {
            val fileNameWithExt = "$fileName.wav"
            return Paths.get(saveDir).resolve(fileNameWithExt)
        }

        fun getRecordingsStoragePath(context: Context): Path {

            if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED)
                error("External storage is not writable")

            val externalStorageVolumes = ContextCompat.getExternalFilesDirs(context, null)
            val primaryExternalStorage = externalStorageVolumes[0]
            val savePath = Paths.get(primaryExternalStorage.path, "CallRecordings")

            if (!Files.exists(savePath))
                Files.createDirectory(savePath)

            return savePath
        }
    }
}

class RecordingStoragePathInitializer @Inject constructor(
    val prefs: Prefs,
) : StartupInitializer {

    override fun initialize(context: Context) = GlobalScope.launchUnit {

        if (prefs.stringOrNull(PREF_RECORDINGS_STORAGE_PATH).first() == null) {

            val newRecordingPath = Recordings.getRecordingsStoragePath(context).toString()

            prefs.editor { set(PREF_RECORDINGS_STORAGE_PATH, newRecordingPath) }
        }
    }
}
