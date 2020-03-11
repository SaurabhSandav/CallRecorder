package com.redridgeapps.callrecorder.utils.prefs

import android.media.AudioFormat
import com.redridgeapps.callrecorder.callutils.RecordingAPI

var prefList: List<TypedPref<*>> = emptyList()
    private set

fun <T : TypedPref<*>> T.addToPrefList(): T {
    prefList = prefList + this
    return this
}

val PREF_IS_FIRST_RUN = PrefBoolean(
    key = "IS_FIRST_RUN",
    defaultValue = true
).addToPrefList()

val PREF_IS_RECORDING_ON = PrefBoolean(
    key = "IS_RECORDING_ON",
    defaultValue = true
).addToPrefList()

val PREF_RECORDING_API = PrefString(
    key = "RECORDING_API",
    defaultValue = RecordingAPI.AudioRecord.toString()
).addToPrefList()

val PREF_MEDIA_RECORDER_CHANNELS = PrefInt(
    key = "MEDIA_RECORDER_CHANNELS",
    defaultValue = 1
).addToPrefList()

val PREF_MEDIA_RECORDER_SAMPLE_RATE = PrefInt(
    key = "MEDIA_RECORDER_SAMPLE_RATE",
    defaultValue = 44_100
).addToPrefList()

val PREF_AUDIO_RECORD_SAMPLE_RATE = PrefInt(
    key = "AUDIO_RECORD_SAMPLE_RATE",
    defaultValue = 44_100
).addToPrefList()

val PREF_AUDIO_RECORD_CHANNEL = PrefInt(
    key = "AUDIO_RECORD_CHANNEL",
    defaultValue = AudioFormat.CHANNEL_IN_MONO
).addToPrefList()

val PREF_AUDIO_RECORD_ENCODING = PrefInt(
    key = "AUDIO_RECORD_ENCODING",
    defaultValue = AudioFormat.ENCODING_PCM_16BIT
).addToPrefList()
