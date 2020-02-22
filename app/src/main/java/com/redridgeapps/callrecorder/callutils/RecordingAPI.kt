package com.redridgeapps.callrecorder.callutils

import androidx.lifecycle.Lifecycle
import com.redridgeapps.callrecorder.callutils.recorder.AudioRecordAPI
import com.redridgeapps.callrecorder.callutils.recorder.MediaRecorderAPI
import com.redridgeapps.callrecorder.callutils.recorder.Recorder

private typealias RecorderInit = (savePath: String, lifecycle: Lifecycle) -> Recorder

enum class RecordingAPI(
    val init: RecorderInit
) {
    MediaRecorder(::MediaRecorderAPI),
    AudioRecord(::AudioRecordAPI)
}
