package com.redridgeapps.callrecorder.db.adapters

import com.squareup.sqldelight.ColumnAdapter
import java.time.Duration

object DurationLongColumnAdapter : ColumnAdapter<Duration, Long> {

    override fun decode(databaseValue: Long): Duration = Duration.ofMillis(databaseValue)

    override fun encode(value: Duration): Long = value.toMillis()
}
