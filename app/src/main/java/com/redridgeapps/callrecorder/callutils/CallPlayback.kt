package com.redridgeapps.callrecorder.callutils

import android.media.MediaPlayer
import com.redridgeapps.callrecorder.Recording
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CallPlayback @Inject constructor() {

    private val _playbackState = BroadcastChannel<PlaybackState>(CONFLATED)
    val playbackState: Flow<PlaybackState> = _playbackState.asFlow()

    init {
        _playbackState.offer(PlaybackState.Stopped(_playbackState))
    }
}

sealed class PlaybackState {

    abstract suspend fun startNewPlayback(recording: Recording)

    class Stopped(
        private val playbackState: BroadcastChannel<PlaybackState>
    ) : PlaybackState() {

        override suspend fun startNewPlayback(recording: Recording) {
            MediaPlayer().startNewPlayback(recording, playbackState)
        }
    }

    sealed class NotStopped(
        protected val player: MediaPlayer,
        protected val playbackState: BroadcastChannel<PlaybackState>
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

            playbackState.offer(Stopped(playbackState))
        }

        class Playing(
            override val recording: Recording,
            player: MediaPlayer,
            playbackState: BroadcastChannel<PlaybackState>
        ) : NotStopped(player, playbackState) {

            override val progress: Flow<Float> = flow {
                while (true) {
                    emit(player.progress)
                    delay(1000)
                }
            }

            fun pausePlayback() {
                player.pause()
                playbackState.offer(Paused(recording, player, playbackState))
            }
        }

        class Paused(
            override val recording: Recording,
            player: MediaPlayer,
            playbackState: BroadcastChannel<PlaybackState>
        ) : NotStopped(player, playbackState) {

            override val progress: Flow<Float> = flowOf(player.progress)

            fun resumePlayback() {
                player.start()
                playbackState.offer(Playing(recording, player, playbackState))
            }
        }
    }
}

private suspend fun MediaPlayer.startNewPlayback(
    recording: Recording,
    playbackState: BroadcastChannel<PlaybackState>
) = withContext(Dispatchers.IO) {

    reset()
    setDataSource(recording.save_path)
    prepare()
    start()

    setOnCompletionListener {
        seekTo(0)
        val paused =
            PlaybackState.NotStopped.Paused(recording, this@startNewPlayback, playbackState)
        playbackState.offer(paused)
    }

    val playing = PlaybackState.NotStopped.Playing(recording, this@startNewPlayback, playbackState)
    playbackState.offer(playing)
}

private val MediaPlayer.progress: Float
    get() = currentPosition / duration.toFloat()

