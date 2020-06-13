package com.redridgeapps.callrecorder.callutils.playback

import android.media.MediaPlayer
import com.redridgeapps.callrecorder.Recording
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CallPlayback @Inject constructor() {

    private val _playbackState = MutableStateFlow<PlaybackState>(PlaybackState.Stopped)

    val playbackState: StateFlow<PlaybackState> = _playbackState

    suspend fun PlaybackState.startNewPlayback(recording: Recording) {
        val player = when (this) {
            PlaybackState.Stopped -> MediaPlayer()
            is PlaybackState.NotStopped -> player
        }

        player.startNewPlayback(recording, _playbackState)
    }

    fun PlaybackState.NotStopped.stopPlayback() {
        player.stop()
        player.reset()
        player.release()

        _playbackState.value = PlaybackState.Stopped
    }

    fun PlaybackState.NotStopped.setPosition(progress: Float) {
        player.seekTo((player.duration * progress).toInt())
    }

    fun PlaybackState.NotStopped.Playing.pausePlayback() {
        player.pause()
        _playbackState.value = PlaybackState.NotStopped.Paused(player, recording, progress)
    }

    fun PlaybackState.NotStopped.Paused.resumePlayback() {
        player.start()
        _playbackState.value = PlaybackState.NotStopped.Playing(player, recording, progress)
    }
}

sealed class PlaybackState {

    object Stopped : PlaybackState()

    sealed class NotStopped : PlaybackState() {

        abstract val player: MediaPlayer

        abstract val recording: Recording

        abstract val progress: Flow<Float>

        class Playing(
            override val player: MediaPlayer,
            override val recording: Recording,
            override val progress: Flow<Float>
        ) : NotStopped()

        class Paused(
            override val player: MediaPlayer,
            override val recording: Recording,
            override val progress: Flow<Float>
        ) : NotStopped()
    }
}

private suspend fun MediaPlayer.startNewPlayback(
    recording: Recording,
    playbackState: MutableStateFlow<PlaybackState>
) = withContext(Dispatchers.IO) {

    reset()
    setDataSource(recording.save_path)
    prepare()
    start()

    val progress = createProgressFlow()

    setOnCompletionListener {
        seekTo(0)
        playbackState.value =
            PlaybackState.NotStopped.Paused(this@startNewPlayback, recording, progress)
    }

    playbackState.value =
        PlaybackState.NotStopped.Playing(this@startNewPlayback, recording, progress)
}

private fun MediaPlayer.createProgressFlow(): Flow<Float> = flow {
    while (true) {
        emit(progress)
        delay(500)
    }
}

private val MediaPlayer.progress: Float
    get() = currentPosition / duration.toFloat()

