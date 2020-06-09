package com.redridgeapps.callrecorder.prefs

sealed class PrefType<T : Any?> {

    object PrefString : PrefType<String>()

    object PrefStringNullable : PrefType<String?>()

    object PrefBoolean : PrefType<Boolean>()

    object PrefInt : PrefType<Int>()

    object PrefLong : PrefType<Long>()

    object PrefFloat : PrefType<Float>()

    class PrefEnum<T : Enum<T>>(val valueOf: (String) -> T) : PrefType<T>()

    companion object {

        @Suppress("FunctionName")
        inline fun <reified T : Enum<T>> PrefEnum(): PrefEnum<T> {
            return PrefEnum(::enumValueOf)
        }
    }
}
