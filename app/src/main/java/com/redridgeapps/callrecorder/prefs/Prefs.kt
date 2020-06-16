@file:Suppress("Unused")

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

    fun <T : String?> prefString(
        key: String,
        default: () -> T
    ): Flow<T> = pref(key, default) {
        @Suppress("UNCHECKED_CAST")
        sharedPrefs.getString(key, "")!! as T
    }

    fun <T : Set<String>?> prefStringSet(
        key: String,
        default: () -> T
    ): Flow<T> = pref(key, default) {
        @Suppress("UNCHECKED_CAST")
        sharedPrefs.getStringSet(key, emptySet())!! as T
    }

    fun <T : Boolean?> prefBoolean(
        key: String,
        default: () -> T
    ): Flow<T> = pref(key, default) {
        @Suppress("UNCHECKED_CAST")
        sharedPrefs.getBoolean(key, false) as T
    }

    fun <T : Int?> prefInt(
        key: String,
        default: () -> T
    ): Flow<T> = pref(key, default) {
        @Suppress("UNCHECKED_CAST")
        sharedPrefs.getInt(key, Int.MAX_VALUE) as T
    }

    fun <T : Long?> prefLong(
        key: String,
        default: () -> T
    ): Flow<T> = pref(key, default) {
        @Suppress("UNCHECKED_CAST")
        sharedPrefs.getLong(key, Long.MAX_VALUE) as T
    }

    fun <T : Float?> prefFloat(
        key: String,
        default: () -> T
    ): Flow<T> = pref(key, default) {
        @Suppress("UNCHECKED_CAST")
        sharedPrefs.getFloat(key, Float.MAX_VALUE) as T
    }

    private fun <T> pref(
        key: String,
        default: () -> T,
        prefGetter: () -> T
    ): Flow<T> {
        return allPrefs
            .filter { it == key }
            .onStart { emit(key) }
            .map { if (sharedPrefs.contains(key)) prefGetter() else default() }
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
    }

    // endregion Editing
}
