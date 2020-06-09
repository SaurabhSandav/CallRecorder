package com.redridgeapps.callrecorder.db.adapter

import com.squareup.sqldelight.ColumnAdapter
import java.time.Instant

object InstantIntegerColumnAdapter : ColumnAdapter<Instant, Long> {

    override fun decode(databaseValue: Long): Instant = Instant.ofEpochMilli(databaseValue)

    override fun encode(value: Instant): Long = value.toEpochMilli()
}
