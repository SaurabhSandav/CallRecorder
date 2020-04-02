package com.redridgeapps.callrecorder.db

import com.squareup.sqldelight.ColumnAdapter

class EnumTextColumnAdapter<T : Enum<T>>(
    private val valueOf: (String) -> T
) : ColumnAdapter<Enum<T>, String> {

    override fun decode(databaseValue: String): Enum<T> = valueOf(databaseValue)

    override fun encode(value: Enum<T>): String = value.name
}
