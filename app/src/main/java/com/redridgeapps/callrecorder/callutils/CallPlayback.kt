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

    suspend fun startPlayback(
        recordingId: Int,
        onComplete: () -> Unit
    ) = withContext(Dispatchers.IO) {

        val recording = recordingQueries.getWithId(recordingId).asFlow().mapToOne().first()
        val recordingPath = recording.save_path

        player = player ?: MediaPlayer()

        player!!.apply {
            reset()
            setDataSource(recordingPath)
            prepare()
            start()
            setOnCompletionListener { onComplete() }
        }

        return@withContext
    }

    fun resumePlayback() {
        player!!.start()
    }

    fun pausePlayback() {
        player!!.pause()
    }

    fun stopPlayback() {
        player!!.stop()
        releasePlayer()
    }

    fun releasePlayer() {
        player?.reset()
        player?.release()
        player = null
    }
}
