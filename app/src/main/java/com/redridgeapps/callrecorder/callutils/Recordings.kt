package com.redridgeapps.callrecorder.callutils

import com.redridgeapps.callrecorder.RecordingQueries
import com.redridgeapps.repository.RecordingItem
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class Recordings @Inject constructor(private val recordingQueries: RecordingQueries) {

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
}
