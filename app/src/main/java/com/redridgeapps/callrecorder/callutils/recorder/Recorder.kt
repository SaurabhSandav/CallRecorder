package com.redridgeapps.callrecorder.callutils.recorder

import java.io.File

interface Recorder {

    val saveFileExt: String

    suspend fun startRecording(saveFile: File)

    fun stopRecording()

    fun releaseRecorder()
}
