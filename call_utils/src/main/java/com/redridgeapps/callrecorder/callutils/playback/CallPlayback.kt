package com.redridgeapps.callrecorder.callutils.playback

import android.media.MediaPlayer
import com.redridgeapps.callrecorder.callutils.db.Recording
import com.redridgeapps.callrecorder.callutils.playback.PlaybackState.Started
import com.redridgeapps.callrecorder.callutils.playback.PlaybackState.Started.Paused
import com.redridgeapps.callrecorder.callutils.playback.PlaybackState.Started.Playing
import com.redridgeapps.callrecorder.callutils.playback.PlaybackState.Stopped
import com.redridgeapps.callrecorder.common.AppDispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CallPlayback @Inject constructor(
    private val dispatchers: AppDispatchers,
) {

    private val _playbackState = MutableStateFlow<PlaybackState>(Stopped)
    val playbackState: StateFlow<PlaybackState> by ::_playbackState

    suspend fun PlaybackState.startNewPlayback(recording: Recording) {
        val player = when (this) {
            Stopped -> MediaPlayer()
            is Started -> player
        }

        player.startNewPlayback(recording, _playbackState, dispatchers)
    }

    fun Started.stopPlayback() {
        player.stop()
        player.reset()
        player.release()

        _playbackState.value = Stopped
    }

    fun Started.setPosition(progress: Float) {
        player.seekTo((player.duration * progress).toInt())
    }

    fun Playing.pausePlayback() {
        player.pause()
        _playbackState.value = Paused(player, recordingId, progress)
    }

    fun Paused.resumePlayback() {
        player.start()
        _playbackState.value = Playing(player, recordingId, progress)
    }
}

private suspend fun MediaPlayer.startNewPlayback(
    recording: Recording,
    playbackState: MutableStateFlow<PlaybackState>,
    dispatchers: AppDispatchers,
) = withContext(dispatchers.IO) {

    reset()
    setDataSource(recording.save_path)
    prepare()
    start()

    val progress = createProgressFlow()

    setOnCompletionListener {
        seekTo(0)
        playbackState.value = Paused(this@startNewPlayback, recording.id, progress)
    }

    playbackState.value = Playing(this@startNewPlayback, recording.id, progress)
}

private fun MediaPlayer.createProgressFlow(): Flow<Float> = flow {
    while (true) {
        emit(progress)
        delay(500)
    }
}

private val MediaPlayer.progress: Float
    get() = currentPosition / duration.toFloat()

