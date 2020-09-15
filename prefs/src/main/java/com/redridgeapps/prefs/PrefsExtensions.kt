package com.redridgeapps.prefs

val Prefs.audioRecord: Prefs.AudioRecord
    get() = audio_record ?: Prefs.AudioRecord()
