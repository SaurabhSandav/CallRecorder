package com.redridgeapps.callrecorder.prefs

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

inline fun <reified T : Enum<T>> Prefs.prefEnum(
    key: String,
    noinline default: () -> T
): Flow<T> = prefEnum(enumValues(), key, default)

fun <T : Enum<T>> Prefs.prefEnum(
    enumValues: Array<T>,
    key: String,
    default: () -> T
): Flow<T> {
    return prefString(key) { default().name }.map { value ->
        enumValues.singleOrNull { enumValue ->
            value == enumValue.name
        } ?: error("Invalid pref value")
    }
}

inline fun <reified T : Enum<T>> Prefs.Editor.setEnum(key: String, value: T) =
    setString(key, value.name)
