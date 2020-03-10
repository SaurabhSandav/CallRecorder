package com.redridgeapps.callrecorder.callutils

import android.content.Context
import android.os.Environment
import androidx.core.content.ContextCompat
import com.redridgeapps.callrecorder.RecordingQueries
import com.redridgeapps.callrecorder.utils.CallLogFetcher
import com.redridgeapps.repository.RecordingItem
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Recordings @Inject constructor(
    private val context: Context,
    private val recordingQueries: RecordingQueries,
    private val callLogFetcher: CallLogFetcher
) {

    fun generateFileName(saveFileExt: String): File {
        val fileName = "${Instant.now().epochSecond}.$saveFileExt"
        return File(getRecordingStoragePath(), fileName)
    }

    fun insertRecording(
        recordingStartTime: Long,
        recordingEndTime: Long,
        saveFile: File
    ) {

        val callEntry = callLogFetcher.getLastCallEntry() ?: error("No call log Found!")

        recordingQueries.insert(
            name = if (callEntry.name.isBlank()) "Unknown" else callEntry.name,
            number = callEntry.number,
            startTime = recordingStartTime,
            endTime = recordingEndTime,
            callType = callEntry.type,
            savePath = saveFile.toString()
        )
    }

    fun getRecordingList(): Flow<List<RecordingItem>> {
        return recordingQueries.getAll { id, name, number, _, _, callType, _ ->
            RecordingItem(
                id = id,
                name = name,
                number = number,
                type = callType
            )
        }.asFlow().mapToList(Dispatchers.IO)
    }

    fun deleteRecording(recordingId: Int) {
        recordingQueries.deleteWithId(recordingId)
    }

    private fun getRecordingStoragePath(): File {

        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED)
            error("External storage is not writable")

        val externalStorageVolumes = ContextCompat.getExternalFilesDirs(context, null)
        val primaryExternalStorage = externalStorageVolumes[0]
        val saveDir = File(primaryExternalStorage, "CallRecordings")

        if (!saveDir.exists())
            saveDir.mkdir()

        return saveDir
    }
}
