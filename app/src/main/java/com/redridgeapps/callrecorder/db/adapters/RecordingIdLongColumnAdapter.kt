package com.redridgeapps.callrecorder.db.adapters

import com.squareup.sqldelight.ColumnAdapter

inline class RecordingId(val value: Long)

// FIXME https://github.com/cashapp/sqldelight/issues/1653
object RecordingIdLongColumnAdapter : ColumnAdapter<RecordingId, Long> {

    override fun decode(databaseValue: Long): RecordingId = RecordingId(databaseValue)

    override fun encode(value: RecordingId): Long = value.value
}
