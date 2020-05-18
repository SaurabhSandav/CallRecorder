package com.redridgeapps.callrecorder.utils

import java.util.*

inline fun <reified T : Enum<T>> enumSetOf(item: T): EnumSet<T> {
    return EnumSet.of(item)
}

inline fun <reified T : Enum<T>> enumSetOfAll(): EnumSet<T> {
    return EnumSet.allOf(T::class.java)
}

inline fun <reified T : Enum<T>> enumSetComplementOf(item: T): EnumSet<T> {
    return enumSetComplementOf(enumSetOf(item))
}

inline fun <reified T : Enum<T>> enumSetComplementOf(set: EnumSet<T>): EnumSet<T> {
    return EnumSet.complementOf(set)
}