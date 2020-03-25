package com.redridgeapps.callrecorder.utils.prefs

import com.redridgeapps.callrecorder.utils.prefs.TypedPref.PrefBoolean
import com.redridgeapps.callrecorder.utils.prefs.TypedPref.PrefEnum
import com.redridgeapps.repository.callutils.AudioRecordChannels
import com.redridgeapps.repository.callutils.AudioRecordEncoding
import com.redridgeapps.repository.callutils.AudioRecordSampleRate

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
    defaultValue = false
).addToPrefList()

val PREF_AUDIO_RECORD_SAMPLE_RATE = PrefEnum(
    key = "AUDIO_RECORD_SAMPLE_RATE",
    defaultValue = AudioRecordSampleRate.S44_100,
    valueOf = ::enumValueOf
).addToPrefList()

val PREF_AUDIO_RECORD_CHANNELS = PrefEnum(
    key = "AUDIO_RECORD_CHANNELS",
    defaultValue = AudioRecordChannels.MONO,
    valueOf = ::enumValueOf
).addToPrefList()

val PREF_AUDIO_RECORD_ENCODING = PrefEnum(
    key = "AUDIO_RECORD_ENCODING",
    defaultValue = AudioRecordEncoding.ENCODING_PCM_16BIT,
    valueOf = ::enumValueOf
).addToPrefList()
