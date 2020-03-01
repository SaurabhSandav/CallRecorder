package com.redridgeapps.callrecorder.callutils

import com.redridgeapps.callrecorder.callutils.recorder.AudioRecordAPI
import com.redridgeapps.callrecorder.callutils.recorder.MediaRecorderAPI
import com.redridgeapps.callrecorder.callutils.recorder.Recorder
import java.io.File

enum class RecordingAPI(
    val init: (saveDir: File) -> Recorder
) {
    MediaRecorder(::MediaRecorderAPI),
    AudioRecord(::AudioRecordAPI)
}
