package com.redridgeapps.callrecorder.callutils

import android.media.MediaPlayer
import com.redridgeapps.callrecorder.RecordingQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CallPlayback @Inject constructor(
    private val recordingQueries: RecordingQueries
) {

    private var player: MediaPlayer? = null
    private var recordingId: RecordingId? = null
    private val playbackStateChannel = BroadcastChannel<PlaybackState>(CONFLATED).apply {
        offer(PlaybackState.Stopped)
    }

    val playbackState: Flow<PlaybackState> = playbackStateChannel.asFlow()

    suspend fun startPlayback(recordingId: RecordingId) = withContext(Dispatchers.IO) {

        val recording = recordingQueries.getWithId(recordingId.value).executeAsOne()
        val recordingPath = recording.save_path

        this@CallPlayback.recordingId = recordingId
        player = player ?: MediaPlayer()

        player!!.apply {
            reset()

            // Player is Idle after reset
            PlaybackState.Stopped.offerTo(playbackStateChannel)

            setDataSource(recordingPath)
            prepare()
            start()

            setOnCompletionListener { PlaybackState.Stopped.offerTo(playbackStateChannel) }
        }

        PlaybackState.Playing(recordingId).offerTo(playbackStateChannel)

        return@withContext
    }

    fun resumePlayback() {
        player!!.start()

        PlaybackState.Playing(recordingId!!).offerTo(playbackStateChannel)
    }

    fun pausePlayback() {
        player!!.pause()

        PlaybackState.Paused(recordingId!!).offerTo(playbackStateChannel)
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

        PlaybackState.Stopped.offerTo(playbackStateChannel)
    }
}

sealed class PlaybackState {
    class Playing(val recordingId: RecordingId) : PlaybackState()
    class Paused(val recordingId: RecordingId) : PlaybackState()
    object Stopped : PlaybackState()
}

fun PlaybackState.offerTo(channel: BroadcastChannel<PlaybackState>) {
    channel.offer(this)
}
