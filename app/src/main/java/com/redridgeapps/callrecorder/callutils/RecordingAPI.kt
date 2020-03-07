package com.redridgeapps.callrecorder.callutils

import android.content.SharedPreferences
import com.redridgeapps.callrecorder.callutils.recorder.AudioRecordAPI
import com.redridgeapps.callrecorder.callutils.recorder.MediaRecorderAPI
import com.redridgeapps.callrecorder.callutils.recorder.Recorder

enum class RecordingAPI(
    val init: (prefs: SharedPreferences) -> Recorder
) {
    MediaRecorder(::MediaRecorderAPI),
    AudioRecord(::AudioRecordAPI)
}
