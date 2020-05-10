package com.redridgeapps.callrecorder.callutils

import android.media.MediaPlayer
import com.redridgeapps.callrecorder.RecordingQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
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

            setOnCompletionListener {
                seekTo(0)

                val currentPlaybackState = _playbackState.value as PlaybackState.NotStopped
                _playbackState.value = PlaybackState.Paused(
                    recordingId = recordingId,
                    name = currentPlaybackState.name,
                    progress = currentPlaybackState.progress
                )
            }
        }

        _playbackState.value = PlaybackState.Playing(recordingId, recording.name, observeProgress())

        return@withContext
    }

    fun resumePlayback() {
        player!!.start()

        val currentPlaybackState = _playbackState.value as PlaybackState.NotStopped
        _playbackState.value = PlaybackState.Playing(
            recordingId = recordingId!!,
            name = currentPlaybackState.name,
            progress = currentPlaybackState.progress
        )
    }

    fun pausePlayback() {
        player!!.pause()

        val currentPlaybackState = _playbackState.value as PlaybackState.NotStopped
        _playbackState.value = PlaybackState.Paused(
            recordingId = recordingId!!,
            name = currentPlaybackState.name,
            progress = currentPlaybackState.progress
        )
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

    private suspend fun observeProgress(): Flow<Float> = flow {
        _playbackState.collect {
            while (_playbackState.value is PlaybackState.Playing) {
                val progressPercent = player!!.currentPosition / player!!.duration.toFloat()
                emit(progressPercent)
                delay(1000)
            }
        }
    }
}

sealed class PlaybackState {

    interface NotStopped {
        val recordingId: RecordingId
        val name: String
        val progress: Flow<Float>
    }

    class Playing(
        override val recordingId: RecordingId,
        override val name: String,
        override val progress: Flow<Float>
    ) : PlaybackState(), NotStopped

    class Paused(
        override val recordingId: RecordingId,
        override val name: String,
        override val progress: Flow<Float>
    ) : PlaybackState(), NotStopped

    object Stopped : PlaybackState()
}
