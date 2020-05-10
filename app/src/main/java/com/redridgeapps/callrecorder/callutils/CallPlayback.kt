package com.redridgeapps.callrecorder.callutils

import android.media.MediaPlayer
import com.redridgeapps.callrecorder.RecordingQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CallPlayback @Inject constructor(
    private val recordingQueries: RecordingQueries
) {

    private var player: MediaPlayer? = null
    private var recordingId: RecordingId? = null
    private val _playbackState = MutableStateFlow<PlaybackState>(PlaybackState.Stopped)

    val playbackState: StateFlow<PlaybackState> = _playbackState

    suspend fun startPlayback(recordingId: RecordingId) = withContext(Dispatchers.IO) {

        val recording = recordingQueries.getWithId(recordingId.value).executeAsOne()
        val recordingPath = recording.save_path

        this@CallPlayback.recordingId = recordingId
        player = player ?: MediaPlayer()

        player!!.apply {
            reset()

            // Player is Idle after reset
            _playbackState.value = PlaybackState.Stopped

            setDataSource(recordingPath)
            prepare()
            start()

            setOnCompletionListener { _playbackState.value = PlaybackState.Stopped }
        }

        _playbackState.value = PlaybackState.Playing(recordingId)

        return@withContext
    }

    fun resumePlayback() {
        player!!.start()

        _playbackState.value = PlaybackState.Playing(recordingId!!)
    }

    fun pausePlayback() {
        player!!.pause()

        _playbackState.value = PlaybackState.Paused(recordingId!!)
    }

    fun stopPlayback() {
        player!!.stop()
        releasePlayer()
    }

    fun releasePlayer() {
        player?.reset()
        player?.release()
        player = null
        recordingId = null

        _playbackState.value = PlaybackState.Stopped
    }
}

sealed class PlaybackState {
    class Playing(val recordingId: RecordingId) : PlaybackState()
    class Paused(val recordingId: RecordingId) : PlaybackState()
    object Stopped : PlaybackState()
}
