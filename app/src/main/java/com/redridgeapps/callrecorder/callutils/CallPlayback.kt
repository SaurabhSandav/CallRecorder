package com.redridgeapps.callrecorder.callutils

import android.media.MediaPlayer
import com.redridgeapps.callrecorder.Recording
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CallPlayback @Inject constructor() {

    private val _playbackState = MutableStateFlow<PlaybackState>(PlaybackState.UnInitialized)
    val playbackState: StateFlow<PlaybackState> = _playbackState

    init {
        _playbackState.value = PlaybackState.Stopped(_playbackState)
    }
}

sealed class PlaybackState {

    abstract suspend fun startNewPlayback(recording: Recording)

    object UnInitialized : PlaybackState() {
        override suspend fun startNewPlayback(recording: Recording): Unit =
            error("PlaybackState is Uninitialized")
    }

    class Stopped(
        private val playbackState: MutableStateFlow<PlaybackState>
    ) : PlaybackState() {

        override suspend fun startNewPlayback(recording: Recording) {
            MediaPlayer().startNewPlayback(recording, playbackState)
        }
    }

    sealed class NotStopped(
        protected val player: MediaPlayer,
        protected val playbackState: MutableStateFlow<PlaybackState>
    ) : PlaybackState() {

        abstract val recording: Recording

        abstract val progress: Flow<Float>

        override suspend fun startNewPlayback(recording: Recording) {
            player.startNewPlayback(recording, playbackState)
        }

        fun stopPlayback() {
            player.stop()
            player.reset()
            player.release()

            playbackState.value = Stopped(playbackState)
        }

        class Playing(
            override val recording: Recording,
            player: MediaPlayer,
            playbackState: MutableStateFlow<PlaybackState>
        ) : NotStopped(player, playbackState) {

            override val progress: Flow<Float> = flow {
                while (true) {
                    emit(player.progress)
                    delay(1000)
                }
            }

            fun pausePlayback() {
                player.pause()
                playbackState.value = Paused(recording, player, playbackState)
            }
        }

        class Paused(
            override val recording: Recording,
            player: MediaPlayer,
            playbackState: MutableStateFlow<PlaybackState>
        ) : NotStopped(player, playbackState) {

            override val progress: Flow<Float> = flowOf(player.progress)

            fun resumePlayback() {
                player.start()
                playbackState.value = Playing(recording, player, playbackState)
            }
        }
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

    setOnCompletionListener {
        seekTo(0)
        playbackState.value =
            PlaybackState.NotStopped.Paused(recording, this@startNewPlayback, playbackState)
    }

    playbackState.value =
        PlaybackState.NotStopped.Playing(recording, this@startNewPlayback, playbackState)
}

private val MediaPlayer.progress: Float
    get() = currentPosition / duration.toFloat()

