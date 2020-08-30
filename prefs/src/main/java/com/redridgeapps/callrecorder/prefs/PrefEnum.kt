@file:Suppress("unused")

package com.redridgeapps.callrecorder.prefs

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

inline fun <reified T : Enum<T>> Prefs.enum(
    key: String,
    noinline default: () -> T
): Flow<T> = enum(enumValues(), key, default)

inline fun <reified T : Enum<T>> Prefs.enumOrNull(
    key: String
): Flow<T?> = enumOrNull<T>(enumValues(), key)

fun <T : Enum<T>> Prefs.enum(
    enumValues: Array<T>,
    key: String,
    default: () -> T
): Flow<T> = stringOrNull(key).map { value ->
    when (value) {
        null -> default()
        else -> enumValues.singleOrNull { enumValue ->
            value == enumValue.name
        } ?: error("prefEnum: No matching Enum found!")
    }
}

fun <T : Enum<T>> Prefs.enumOrNull(
    enumValues: Array<T>,
    key: String
): Flow<T?> = stringOrNull(key).map { value ->
    when (value) {
        null -> null
        else -> enumValues.singleOrNull { enumValue ->
            value == enumValue.name
        } ?: error("prefEnum: No matching Enum found!")
    }
}

inline fun <reified T : Enum<T>> Prefs.Editor.set(key: String, value: T) = set(key, value.name)
