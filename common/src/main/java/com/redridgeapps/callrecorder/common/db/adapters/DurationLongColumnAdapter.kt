package com.redridgeapps.callrecorder.common.db.adapters

import com.squareup.sqldelight.ColumnAdapter
import kotlin.time.Duration
import kotlin.time.milliseconds

object DurationLongColumnAdapter : ColumnAdapter<Duration, Long> {

    override fun decode(databaseValue: Long): Duration = databaseValue.milliseconds

    override fun encode(value: Duration): Long = value.toLongMilliseconds()
}
