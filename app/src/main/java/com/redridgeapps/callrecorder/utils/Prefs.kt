package com.redridgeapps.callrecorder.utils

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.media.AudioFormat
import com.redridgeapps.callrecorder.callutils.RecordingAPI
import javax.inject.Inject

class Prefs @Inject constructor(
    val sharedPreferences: SharedPreferences
) {

    private val listeners = mutableListOf<(key: String) -> Unit>()
    private val preferenceListener = OnSharedPreferenceChangeListener { _, key ->
        listeners.forEach { listener -> listener(key) }
    }

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceListener)
    }

    inline fun <reified T> get(pref: Pref<T>): T = with(sharedPreferences) {

        val result: Any = when (T::class) {
            String::class -> getString(pref.key, pref.defaultValue as String)!!
            Boolean::class -> getBoolean(pref.key, pref.defaultValue as Boolean)
            Int::class -> getInt(pref.key, pref.defaultValue as Int)
            Long::class -> getLong(pref.key, pref.defaultValue as Long)
            Float::class -> getFloat(pref.key, pref.defaultValue as Float)
            else -> error("Unsupported class")
        }

        return result as T
    }

    inline fun <reified T> modify(pref: Pref<T>, newValue: T) {

        val editor = sharedPreferences.edit()

        when (T::class) {
            String::class -> editor.putString(pref.key, newValue as String)
            Boolean::class -> editor.putBoolean(pref.key, newValue as Boolean)
            Int::class -> editor.putInt(pref.key, newValue as Int)
            Long::class -> editor.putLong(pref.key, newValue as Long)
            Float::class -> editor.putFloat(pref.key, newValue as Float)
            else -> error("Unsupported class")
        }

        editor.apply()
    }

    fun addListener(listener: (key: String) -> Unit) {
        listeners.add(listener)
    }

    fun removeListener(listener: (key: String) -> Unit) {
        listeners.remove(listener)
    }
}

class Pref<T>(
    val key: String,
    val defaultValue: T
)

val PREF_IS_FIRST_RUN = Pref(
    key = "IS_FIRST_RUN",
    defaultValue = true
)

val PREF_IS_RECORDING_ON = Pref(
    key = "IS_RECORDING_ON",
    defaultValue = true
)

val PREF_RECORDING_API = Pref(
    key = "RECORDING_API",
    defaultValue = RecordingAPI.AudioRecord.toString()
)

val PREF_MEDIA_RECORDER_CHANNELS = Pref(
    key = "MEDIA_RECORDER_CHANNELS",
    defaultValue = 1
)

val PREF_MEDIA_RECORDER_SAMPLE_RATE = Pref(
    key = "MEDIA_RECORDER_SAMPLE_RATE",
    defaultValue = 44_100
)

val PREF_AUDIO_RECORD_SAMPLE_RATE = Pref(
    key = "AUDIO_RECORD_SAMPLE_RATE",
    defaultValue = 44_100
)

val PREF_AUDIO_RECORD_CHANNEL = Pref(
    key = "AUDIO_RECORD_CHANNEL",
    defaultValue = AudioFormat.CHANNEL_IN_MONO
)

val PREF_AUDIO_RECORD_ENCODING = Pref(
    key = "AUDIO_RECORD_ENCODING",
    defaultValue = AudioFormat.ENCODING_PCM_16BIT
)
