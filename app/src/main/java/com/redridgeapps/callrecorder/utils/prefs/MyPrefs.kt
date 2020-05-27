package com.redridgeapps.callrecorder.utils.prefs

import com.redridgeapps.callrecorder.callutils.PcmChannels
import com.redridgeapps.callrecorder.callutils.PcmEncoding
import com.redridgeapps.callrecorder.callutils.PcmSampleRate
import com.redridgeapps.callrecorder.utils.prefs.PrefType.Companion.PrefEnum
import com.redridgeapps.callrecorder.utils.prefs.PrefType.PrefBoolean
import com.redridgeapps.callrecorder.utils.prefs.PrefType.PrefString

enum class MyPrefs(val type: PrefType<*>) {

    IS_FIRST_RUN(PrefBoolean),

    IS_RECORDING_ON(PrefBoolean),

    RECORDING_PATH(PrefString),

    AUDIO_RECORD_SAMPLE_RATE(PrefEnum<PcmSampleRate>()),

    AUDIO_RECORD_CHANNELS(PrefEnum<PcmChannels>()),

    AUDIO_RECORD_ENCODING(PrefEnum<PcmEncoding>()),
}