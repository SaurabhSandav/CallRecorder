package com.redridgeapps.callrecorder.callutils

import android.media.MediaPlayer
import com.redridgeapps.callrecorder.RecordingQueries
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CallPlayback @Inject constructor(
    private val recordingQueries: RecordingQueries
) {

    private var player: MediaPlayer? = null

    suspend fun startPlaying(
        recordingId: Int,
        onComplete: () -> Unit
    ) = withContext(Dispatchers.IO) {

        val recording = recordingQueries.getWithId(recordingId).asFlow().mapToOne().first()
        val recordingPath = recording.savePath

        player = MediaPlayer().apply {
            setDataSource(recordingPath)
            prepare()
            start()
            setOnCompletionListener { onComplete() }
        }

        return@withContext
    }

    fun stopPlaying() {
        player?.stop()
        player?.release()
        player = null
    }

    fun releasePlayer() {
        player?.release()
        player = null
    }
}
