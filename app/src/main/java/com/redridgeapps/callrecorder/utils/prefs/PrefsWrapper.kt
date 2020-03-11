package com.redridgeapps.callrecorder.utils.prefs

import android.annotation.SuppressLint
import android.content.SharedPreferences
import javax.inject.Inject

class Prefs @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {

    private val listeners = mutableListOf<(TypedPref<*>) -> Unit>()
    private val preferenceListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        prefList.firstOrNull { key == it.key }?.let {
            listeners.forEach { listener -> listener(it) }
        }
    }

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceListener)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any?> get(typedPref: TypedPref<T>): T = with(sharedPreferences) {
        return when (typedPref) {
            is PrefString -> getString(typedPref.key, typedPref.defaultValue) as T
            is PrefStringNullable -> getString(typedPref.key, typedPref.defaultValue) as T
            is PrefBoolean -> getBoolean(typedPref.key, typedPref.defaultValue) as T
            is PrefInt -> getInt(typedPref.key, typedPref.defaultValue) as T
            is PrefLong -> getLong(typedPref.key, typedPref.defaultValue) as T
            is PrefFloat -> getFloat(typedPref.key, typedPref.defaultValue) as T
            else -> error("Unsupported class")
        }
    }

    @SuppressLint("ApplySharedPref")
    fun <T> set(typedPref: TypedPref<T>, newValue: T, commit: Boolean = false) {

        val editor = sharedPreferences.edit()

        when (typedPref) {
            is PrefString, is PrefStringNullable -> editor.putString(
                typedPref.key,
                newValue as String?
            )
            is PrefBoolean -> editor.putBoolean(typedPref.key, newValue as Boolean)
            is PrefInt -> editor.putInt(typedPref.key, newValue as Int)
            is PrefLong -> editor.putLong(typedPref.key, newValue as Long)
            is PrefFloat -> editor.putFloat(typedPref.key, newValue as Float)
            else -> error("Unsupported class")
        }

        if (commit)
            editor.commit()
        else
            editor.apply()
    }

    fun edit(block: Editor.() -> Unit) {

        Editor(sharedPreferences).run {
            block()
            apply()
        }
    }

    fun addListener(listener: (TypedPref<*>) -> Unit) {
        listeners.add(listener)
    }

    fun removeListener(listener: (TypedPref<*>) -> Unit) {
        listeners.remove(listener)
    }

    class Editor(sharedPreferences: SharedPreferences) {

        private val editor = sharedPreferences.edit()

        fun <T> put(typedPref: TypedPref<T>, newValue: T) {

            when (typedPref) {
                is PrefString, is PrefStringNullable -> editor.putString(
                    typedPref.key,
                    newValue as String?
                )
                is PrefBoolean -> editor.putBoolean(typedPref.key, newValue as Boolean)
                is PrefInt -> editor.putInt(typedPref.key, newValue as Int)
                is PrefLong -> editor.putLong(typedPref.key, newValue as Long)
                is PrefFloat -> editor.putFloat(typedPref.key, newValue as Float)
                else -> error("Unsupported class")
            }
        }

        fun apply() {
            editor.apply()
        }

        fun commit() {
            editor.commit()
        }
    }
}
