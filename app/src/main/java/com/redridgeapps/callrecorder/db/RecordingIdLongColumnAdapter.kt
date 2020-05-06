package com.redridgeapps.callrecorder.db

import com.redridgeapps.callrecorder.callutils.RecordingId
import com.squareup.sqldelight.ColumnAdapter

// FIXME https://github.com/cashapp/sqldelight/issues/1653
object RecordingIdLongColumnAdapter : ColumnAdapter<RecordingId, Long> {

    override fun decode(databaseValue: Long): RecordingId = RecordingId(databaseValue)

    override fun encode(value: RecordingId): Long = value.value
}