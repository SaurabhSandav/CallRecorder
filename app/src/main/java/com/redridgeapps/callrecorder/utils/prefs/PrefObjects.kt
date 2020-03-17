package com.redridgeapps.callrecorder.utils.prefs

import com.redridgeapps.repository.callutils.*

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
    defaultValue = RecordingAPI.AUDIO_RECORD.toString()
).addToPrefList()

val PREF_MEDIA_RECORDER_SAMPLE_RATE = PrefInt(
    key = "MEDIA_RECORDER_SAMPLE_RATE",
    defaultValue = MediaRecorderSampleRate.S44_100.sampleRate
).addToPrefList()

val PREF_MEDIA_RECORDER_CHANNELS = PrefInt(
    key = "MEDIA_RECORDER_CHANNELS",
    defaultValue = MediaRecorderChannels.MONO.numChannels
).addToPrefList()

val PREF_AUDIO_RECORD_SAMPLE_RATE = PrefInt(
    key = "AUDIO_RECORD_SAMPLE_RATE",
    defaultValue = AudioRecordSampleRate.S44_100.sampleRate
).addToPrefList()

val PREF_AUDIO_RECORD_CHANNELS = PrefInt(
    key = "AUDIO_RECORD_CHANNELS",
    defaultValue = AudioRecordChannels.MONO.numChannels
).addToPrefList()

val PREF_AUDIO_RECORD_ENCODING = PrefString(
    key = "AUDIO_RECORD_ENCODING",
    defaultValue = AudioRecordEncoding.ENCODING_PCM_16BIT.toString()
).addToPrefList()
