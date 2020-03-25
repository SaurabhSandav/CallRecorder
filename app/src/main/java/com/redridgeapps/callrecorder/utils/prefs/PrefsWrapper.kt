package com.redridgeapps.callrecorder.utils.prefs

import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

class Prefs @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    private val prefsChannel = BroadcastChannel<TypedPref<*>>(Channel.BUFFERED)
    private val preferenceListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        prefList.firstOrNull { key == it.key }
            ?.let { prefsChannel.offer(it) }
    }

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceListener)
    }

    suspend fun <T : Any?> get(pref: TypedPref<T>): T {
        return getFlow(pref).first()
    }

    fun <T : Any?> getFlow(pref: TypedPref<T>): Flow<T> {
        return prefsChannel.asFlow()
            .filter { it == pref }
            .onStart { emit(pref) }
            .map { getInternal(pref) }
    }

    suspend fun <T : Any?> set(pref: TypedPref<T>, newValue: T) {
        withContext(Dispatchers.IO) {

            val editor = sharedPreferences.edit()

            when (pref) {
                is PrefString -> editor.putString(pref.key, newValue as String)
                is PrefStringNullable -> editor.putString(pref.key, newValue as String?)
                is PrefBoolean -> editor.putBoolean(pref.key, newValue as Boolean)
                is PrefInt -> editor.putInt(pref.key, newValue as Int)
                is PrefLong -> editor.putLong(pref.key, newValue as Long)
                is PrefFloat -> editor.putFloat(pref.key, newValue as Float)
                is PrefEnum -> editor.putString(pref.key, newValue.toString())
                else -> error("Unsupported class")
            }

            editor.commit()
        }
    }

    suspend fun edit(block: Editor.() -> Unit) {
        withContext(Dispatchers.IO) {
            Editor(sharedPreferences).run {
                block()
                commit()
            }
        }
    }

    private fun <T : Any?> getInternal(pref: TypedPref<T>): T = with(sharedPreferences) {
        @Suppress("UNCHECKED_CAST")
        return when (pref) {
            is PrefString -> getString(pref.key, pref.defaultValue) as T
            is PrefStringNullable -> getString(pref.key, pref.defaultValue) as T
            is PrefBoolean -> getBoolean(pref.key, pref.defaultValue) as T
            is PrefInt -> getInt(pref.key, pref.defaultValue) as T
            is PrefLong -> getLong(pref.key, pref.defaultValue) as T
            is PrefFloat -> getFloat(pref.key, pref.defaultValue) as T
            is PrefEnum -> pref.valueOf(getString(pref.key, pref.defaultValue.toString())!!)
            else -> error("Unsupported class")
        }
    }

    class Editor(sharedPreferences: SharedPreferences) {

        private val editor = sharedPreferences.edit()

        fun <T : Any?> put(pref: TypedPref<T>, newValue: T) {

            when (pref) {
                is PrefString -> editor.putString(pref.key, newValue as String)
                is PrefStringNullable -> editor.putString(pref.key, newValue as String?)
                is PrefBoolean -> editor.putBoolean(pref.key, newValue as Boolean)
                is PrefInt -> editor.putInt(pref.key, newValue as Int)
                is PrefLong -> editor.putLong(pref.key, newValue as Long)
                is PrefFloat -> editor.putFloat(pref.key, newValue as Float)
                is PrefEnum -> editor.putString(pref.key, newValue.toString())
                else -> error("Unsupported class")
            }
        }

        fun commit() {
            editor.commit()
        }
    }
}
