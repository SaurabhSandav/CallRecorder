@file:Suppress("Unused")

package com.redridgeapps.prefs

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
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

    fun stringOrNull(
        key: String,
    ): Flow<String?> = pref<String?>(key) { sharedPrefs.getString(key, "")!! }

    fun stringSetOrNull(
        key: String,
    ): Flow<Set<String>?> = pref<Set<String>?>(key) { sharedPrefs.getStringSet(key, emptySet())!! }

    fun booleanOrNull(
        key: String,
    ): Flow<Boolean?> = pref(key) { sharedPrefs.getBoolean(key, false) }

    fun intOrNull(
        key: String,
    ): Flow<Int?> = pref(key) { sharedPrefs.getInt(key, Int.MAX_VALUE) }

    fun longOrNull(
        key: String,
    ): Flow<Long?> = pref(key) { sharedPrefs.getLong(key, Long.MAX_VALUE) }

    fun floatOrNull(
        key: String,
    ): Flow<Float?> = pref(key) { sharedPrefs.getFloat(key, Float.MAX_VALUE) }

    fun string(
        key: String,
        default: () -> String,
    ): Flow<String> = stringOrNull(key).map { it ?: default() }

    fun stringSet(
        key: String,
        default: () -> Set<String>,
    ): Flow<Set<String>> = stringSetOrNull(key).map { it ?: default() }

    fun boolean(
        key: String,
        default: () -> Boolean,
    ): Flow<Boolean> = booleanOrNull(key).map { it ?: default() }

    fun int(
        key: String,
        default: () -> Int,
    ): Flow<Int> = intOrNull(key).map { it ?: default() }

    fun long(
        key: String,
        default: () -> Long,
    ): Flow<Long> = longOrNull(key).map { it ?: default() }

    fun float(
        key: String,
        default: () -> Float,
    ): Flow<Float> = floatOrNull(key).map { it ?: default() }

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

        fun set(key: String, value: String) {
            editor.putString(key, value)
        }

        fun set(key: String, value: Boolean) {
            editor.putBoolean(key, value)
        }

        fun set(key: String, value: Int) {
            editor.putInt(key, value)
        }

        fun set(key: String, value: Long) {
            editor.putLong(key, value)
        }

        fun set(key: String, value: Float) {
            editor.putFloat(key, value)
        }

        fun set(key: String, value: Set<String>) {
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
