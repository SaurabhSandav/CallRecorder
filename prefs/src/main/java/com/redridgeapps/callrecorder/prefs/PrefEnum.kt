@file:Suppress("unused")

package com.redridgeapps.callrecorder.prefs

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

inline fun <reified T : Enum<T>> Prefs.prefEnum(
    key: String,
    noinline default: () -> T
): Flow<T> = prefEnum(enumValues(), key, default)

inline fun <reified T : Enum<T>> Prefs.prefEnumOrNull(
    key: String
): Flow<T?> = prefEnumOrNull<T>(enumValues(), key)

fun <T : Enum<T>> Prefs.prefEnum(
    enumValues: Array<T>,
    key: String,
    default: () -> T
): Flow<T> = prefStringOrNull(key).map { value ->
    when (value) {
        null -> default()
        else -> enumValues.singleOrNull { enumValue ->
            value == enumValue.name
        } ?: error("prefEnum: No matching Enum found!")
    }
}

fun <T : Enum<T>> Prefs.prefEnumOrNull(
    enumValues: Array<T>,
    key: String
): Flow<T?> = prefStringOrNull(key).map { value ->
    when (value) {
        null -> null
        else -> enumValues.singleOrNull { enumValue ->
            value == enumValue.name
        } ?: error("prefEnum: No matching Enum found!")
    }
}

inline fun <reified T : Enum<T>> Prefs.Editor.setEnum(key: String, value: T) =
    setString(key, value.name)
