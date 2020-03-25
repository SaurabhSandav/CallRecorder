package com.redridgeapps.callrecorder.utils.prefs

sealed class TypedPref<T : Any?> {
    abstract val key: String
    abstract val defaultValue: T

    class PrefString(
        override val key: String,
        override val defaultValue: String
    ) : TypedPref<String>()

    class PrefStringNullable(
        override val key: String,
        override val defaultValue: String?
    ) : TypedPref<String?>()

    class PrefBoolean(
        override val key: String,
        override val defaultValue: Boolean
    ) : TypedPref<Boolean>()

    class PrefInt(
        override val key: String,
        override val defaultValue: Int
    ) : TypedPref<Int>()

    class PrefLong(
        override val key: String,
        override val defaultValue: Long
    ) : TypedPref<Long>()

    class PrefFloat(
        override val key: String,
        override val defaultValue: Float
    ) : TypedPref<Float>()

    class PrefEnum<T : Enum<T>>(
        override val key: String,
        override val defaultValue: T,
        val valueOf: (String) -> T
    ) : TypedPref<T>()
}
