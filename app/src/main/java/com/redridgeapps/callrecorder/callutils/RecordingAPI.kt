package com.redridgeapps.callrecorder.callutils

import android.content.SharedPreferences
import com.redridgeapps.callrecorder.callutils.recorder.AudioRecordAPI
import com.redridgeapps.callrecorder.callutils.recorder.MediaRecorderAPI
import com.redridgeapps.callrecorder.callutils.recorder.Recorder
import java.io.File

enum class RecordingAPI(
    val init: (saveDir: File, prefs: SharedPreferences) -> Recorder
) {
    MediaRecorder(::MediaRecorderAPI),
    AudioRecord(::AudioRecordAPI)
}
