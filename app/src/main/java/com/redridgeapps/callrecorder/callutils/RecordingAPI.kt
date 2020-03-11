package com.redridgeapps.callrecorder.callutils

import com.redridgeapps.callrecorder.callutils.recorder.AudioRecordAPI
import com.redridgeapps.callrecorder.callutils.recorder.MediaRecorderAPI
import com.redridgeapps.callrecorder.callutils.recorder.Recorder
import com.redridgeapps.callrecorder.utils.prefs.Prefs

enum class RecordingAPI(
    val init: (prefs: Prefs) -> Recorder
) {
    MediaRecorder(::MediaRecorderAPI),
    AudioRecord(::AudioRecordAPI)
}
