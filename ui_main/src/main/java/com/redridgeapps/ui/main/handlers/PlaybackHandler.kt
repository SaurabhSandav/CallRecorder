package com.redridgeapps.ui.main.handlers

import com.redridgeapps.callrecorder.callutils.playback.CallPlayback
import com.redridgeapps.callrecorder.callutils.playback.PlaybackState
import com.redridgeapps.callrecorder.callutils.playback.PlaybackState.NotStopped
import com.redridgeapps.callrecorder.callutils.playback.PlaybackState.NotStopped.Paused
import com.redridgeapps.callrecorder.callutils.playback.PlaybackState.NotStopped.Playing
import com.redridgeapps.callrecorder.callutils.storage.Recordings
import com.redridgeapps.callrecorder.common.ViewModelHandle
import com.redridgeapps.callrecorder.common.utils.launchUnit
import com.redridgeapps.ui.main.CurrentPlayback
import com.redridgeapps.ui.main.RecordingId
import com.redridgeapps.ui.main.SetState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal class PlaybackHandler(
    private val viewModelHandle: ViewModelHandle,
    private val setState: SetState,
    private val recordings: Recordings,
    private val callPlayback: CallPlayback,
) {

    init {
        viewModelHandle.onInit { observePlayback() }
        viewModelHandle.onClear { stopPlayback() }
    }

    private fun observePlayback() {

        callPlayback.playbackState.onEach {

            val currentPlayback = when (it) {
                PlaybackState.Stopped -> null
                is NotStopped -> CurrentPlayback(
                    title = recordings.getRecording(it.recording.id).first().name,
                    isPlaying = it is Playing,
                    positionFlow = it.progress,
                    onPlayPauseToggle = { onPlayPauseToggle(it.recording.id) },
                    onPlaybackStop = this::stopPlayback,
                    onPlaybackSeek = this::onPlaybackSeek,
                )
            }

            setState { copy(currentPlayback = currentPlayback) }

        }.launchIn(viewModelHandle.coroutineScope)
    }

    internal fun onPlayPauseToggle(
        recordingId: RecordingId,
    ) = viewModelHandle.coroutineScope.launchUnit {

        val playbackState = callPlayback.playbackState.value

        when {
            playbackState is Playing && playbackState.recording.id == recordingId -> pausePlayback()
            else -> startPlayback(recordingId)
        }
    }

    private suspend fun startPlayback(recordingId: RecordingId) {

        val recording = recordings.getRecording(recordingId).first()
        val playbackStatus = callPlayback.playbackState.first()

        with(callPlayback) {
            when {
                playbackStatus is Paused && playbackStatus.recording.id == recording.id -> {
                    playbackStatus.resumePlayback()
                }
                else -> playbackStatus.startNewPlayback(recording)
            }
        }
    }

    private suspend fun pausePlayback() {

        val playbackStatus = callPlayback.playbackState.first()

        with(callPlayback) {
            (playbackStatus as? Playing)?.pausePlayback()
        }
    }

    private fun onPlaybackSeek(position: Float) = viewModelHandle.coroutineScope.launchUnit {

        val playbackStatus = callPlayback.playbackState.first()

        with(callPlayback) {
            (playbackStatus as? NotStopped)?.setPosition(position)
        }
    }

    private fun stopPlayback() = viewModelHandle.coroutineScope.launchUnit {

        val playbackStatus = callPlayback.playbackState.first()

        with(callPlayback) {
            (playbackStatus as? NotStopped)?.stopPlayback()
        }
    }
}
