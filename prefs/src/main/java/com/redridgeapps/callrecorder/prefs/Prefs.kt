@file:Suppress("Unused", "MemberVisibilityCanBePrivate")

package com.redridgeapps.callrecorder.prefs

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Prefs @Inject constructor(private val sharedPrefs: SharedPreferences) {

    private val prefsChannel = BroadcastChannel<String>(Channel.BUFFERED)

    // Do not inline. SharedPreferences keeps only a WeakReference.
    private val preferenceListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        GlobalScope.launch {
            prefsChannel.send(key)
        }
    }

    init {
        sharedPrefs.registerOnSharedPreferenceChangeListener(preferenceListener)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    val allPrefs = prefsChannel.asFlow()

    // region Fetching

    fun prefStringOrNull(
        key: String
    ): Flow<String?> = pref<String?>(key) { sharedPrefs.getString(key, "")!! }

    fun prefStringSetOrNull(
        key: String
    ): Flow<Set<String>?> = pref<Set<String>?>(key) { sharedPrefs.getStringSet(key, emptySet())!! }

    fun prefBooleanOrNull(
        key: String
    ): Flow<Boolean?> = pref(key) { sharedPrefs.getBoolean(key, false) }

    fun prefIntOrNull(
        key: String
    ): Flow<Int?> = pref(key) { sharedPrefs.getInt(key, Int.MAX_VALUE) }

    fun prefLongOrNull(
        key: String
    ): Flow<Long?> = pref(key) { sharedPrefs.getLong(key, Long.MAX_VALUE) }

    fun prefFloatOrNull(
        key: String
    ): Flow<Float?> = pref(key) { sharedPrefs.getFloat(key, Float.MAX_VALUE) }

    fun prefString(
        key: String,
        default: () -> String
    ): Flow<String> = prefStringOrNull(key).map { it ?: default() }

    fun prefStringSet(
        key: String,
        default: () -> Set<String>
    ): Flow<Set<String>> = prefStringSetOrNull(key).map { it ?: default() }

    fun prefBoolean(
        key: String,
        default: () -> Boolean
    ): Flow<Boolean> = prefBooleanOrNull(key).map { it ?: default() }

    fun prefInt(
        key: String,
        default: () -> Int
    ): Flow<Int> = prefIntOrNull(key).map { it ?: default() }

    fun prefLong(
        key: String,
        default: () -> Long
    ): Flow<Long> = prefLongOrNull(key).map { it ?: default() }

    fun prefFloat(
        key: String,
        default: () -> Float
    ): Flow<Float> = prefFloatOrNull(key).map { it ?: default() }

    private fun <T> pref(key: String, prefGetter: () -> T): Flow<T?> {
        return allPrefs
            .filter { it == key }
            .onStart { emit(key) }
            .map { if (sharedPrefs.contains(key)) prefGetter() else null }
    }

    // endregion Fetching

    // region Editing

    fun editor(block: Editor.() -> Unit) {
        sharedPrefs.edit {
            Editor(this).block()
        }
    }

    class Editor(private val editor: SharedPreferences.Editor) {

        fun setString(key: String, value: String) {
            editor.putString(key, value)
        }

        fun setBoolean(key: String, value: Boolean) {
            editor.putBoolean(key, value)
        }

        fun setInt(key: String, value: Int) {
            editor.putInt(key, value)
        }

        fun setLong(key: String, value: Long) {
            editor.putLong(key, value)
        }

        fun setFloat(key: String, value: Float) {
            editor.putFloat(key, value)
        }

        fun setStringSet(key: String, value: Set<String>) {
            editor.putStringSet(key, value)
        }

        fun remove(key: String) {
            editor.remove(key)
        }

        fun clear() {
            editor.clear()
        }
    }

    // endregion Editing
}
