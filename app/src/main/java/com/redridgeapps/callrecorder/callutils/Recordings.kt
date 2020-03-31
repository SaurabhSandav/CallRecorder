package com.redridgeapps.callrecorder.callutils

import android.content.Context
import android.os.Environment
import androidx.core.content.ContextCompat
import com.redridgeapps.callrecorder.Recording
import com.redridgeapps.callrecorder.RecordingQueries
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
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
        callType: String,
        recordingStartInstant: Instant,
        recordingEndInstant: Instant,
        savePath: Path
    ) {

        val name = contactNameFetcher.getContactName(phoneNumber) ?: "Unknown ($phoneNumber)"

        recordingQueries.insert(
            name = name,
            number = phoneNumber,
            startInstant = recordingStartInstant,
            endInstant = recordingEndInstant,
            callType = callType,
            savePath = savePath.toString(),
            saveFormat = savePath.toString().substringAfterLast('.', "")
        )
    }

    fun getRecordingList(): Flow<List<Recording>> {
        return recordingQueries.getAll().asFlow().mapToList(Dispatchers.IO)
    }

    fun deleteRecording(recordingId: Int) {
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
}
