package com.redridgeapps.common.db.adapters

import com.squareup.sqldelight.ColumnAdapter
import kotlinx.datetime.Instant

object InstantIntegerColumnAdapter : ColumnAdapter<Instant, Long> {

    override fun decode(databaseValue: Long): Instant = Instant.fromEpochMilliseconds(databaseValue)

    override fun encode(value: Instant): Long = value.toEpochMilliseconds()
}
