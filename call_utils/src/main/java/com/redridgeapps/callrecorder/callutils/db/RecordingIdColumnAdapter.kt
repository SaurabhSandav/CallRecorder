package com.redridgeapps.callrecorder.callutils.db

import com.squareup.sqldelight.ColumnAdapter

object RecordingIdColumnAdapter : ColumnAdapter<RecordingId, Long> {

    override fun decode(databaseValue: Long): RecordingId = RecordingId(databaseValue)

    override fun encode(value: RecordingId): Long = value.value
}
