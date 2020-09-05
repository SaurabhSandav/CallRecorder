package com.redridgeapps.callutils.playback

import android.media.MediaPlayer
import com.redridgeapps.callutils.db.RecordingId
import kotlinx.coroutines.flow.Flow

sealed class PlaybackState {

    object Stopped : PlaybackState()

    sealed class Started : PlaybackState() {

        internal abstract val player: MediaPlayer
        abstract val recordingId: RecordingId
        abstract val progress: Flow<Float>

        class Playing(
            override val player: MediaPlayer,
            override val recordingId: RecordingId,
            override val progress: Flow<Float>,
        ) : Started()

        class Paused(
            override val player: MediaPlayer,
            override val recordingId: RecordingId,
            override val progress: Flow<Float>,
        ) : Started()
    }
}
