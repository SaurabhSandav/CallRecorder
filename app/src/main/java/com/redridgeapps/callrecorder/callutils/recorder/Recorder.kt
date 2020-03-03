package com.redridgeapps.callrecorder.callutils.recorder

interface Recorder {

    fun startRecording(fileName: String)

    fun stopRecording(): String

    fun releaseRecorder()
}
