package com.redridgeapps.callrecorder.prefs

import android.annotation.SuppressLint
import android.content.SharedPreferences
import com.redridgeapps.callrecorder.prefs.PrefType.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

class Prefs @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    private val prefsChannel = BroadcastChannel<MyPrefs>(Channel.BUFFERED)

    // Do not inline. SharedPreferences keeps only a WeakReference.
    private val preferenceListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        enumValues<MyPrefs>().firstOrNull { key == it.name }?.let { prefsChannel.offer(it) }
    }

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceListener)
    }

    suspend fun <T : Any?> get(pref: MyPrefs, defaultGenerator: () -> T): T {
        return getFlow(pref, defaultGenerator).first()
    }

    fun <T : Any?> getFlow(
        pref: MyPrefs,
        defaultGenerator: () -> T
    ): Flow<T> {
        return prefsChannel.asFlow()
            .filter { it == pref }
            .onStart { emit(pref) }
            .map { getOrDefault(pref, defaultGenerator) }
            .conflate()
    }

    suspend fun <T : Any?> set(pref: MyPrefs, newValue: T) {
        edit { put(pref, newValue) }
    }

    @SuppressLint("ApplySharedPref")
    suspend fun edit(block: Editor.() -> Unit) = withContext(Dispatchers.IO) {

        val editor = sharedPreferences.edit()

        Editor(editor).block()

        editor.commit()

        return@withContext
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any?> getOrDefault(
        pref: MyPrefs,
        defaultGenerator: () -> T
    ): T = with(sharedPreferences) {

        if (!contains(pref.name)) return@with defaultGenerator()

        // When the key doesn't exist, method will return a defaultValue from above if-check
        // Use garbage default values below. They'll never be returned.
        return when (pref.type) {
            is PrefString -> getString(pref.name, null)
            is PrefStringNullable -> getString(pref.name, null)
            is PrefBoolean -> getBoolean(pref.name, false)
            is PrefInt -> getInt(pref.name, Int.MAX_VALUE)
            is PrefLong -> getLong(pref.name, Long.MAX_VALUE)
            is PrefFloat -> getFloat(pref.name, Float.MAX_VALUE)
            is PrefEnum -> pref.type.valueOf(getString(pref.name, null)!!)
        } as T
    }

    class Editor(private val editor: SharedPreferences.Editor) {

        fun <T : Any?> put(pref: MyPrefs, newValue: T) {

            when (pref.type) {
                is PrefString -> editor.putString(pref.name, newValue as String)
                is PrefStringNullable -> editor.putString(pref.name, newValue as String?)
                is PrefBoolean -> editor.putBoolean(pref.name, newValue as Boolean)
                is PrefInt -> editor.putInt(pref.name, newValue as Int)
                is PrefLong -> editor.putLong(pref.name, newValue as Long)
                is PrefFloat -> editor.putFloat(pref.name, newValue as Float)
                is PrefEnum -> editor.putString(pref.name, (newValue as Enum<*>).name)
            }
        }
    }
}
