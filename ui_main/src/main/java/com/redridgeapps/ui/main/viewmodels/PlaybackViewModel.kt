package com.redridgeapps.ui.main.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redridgeapps.callrecorder.callutils.playback.CallPlayback
import com.redridgeapps.callrecorder.callutils.playback.PlaybackState
import com.redridgeapps.callrecorder.callutils.storage.Recordings
import com.redridgeapps.callrecorder.common.utils.launchUnit
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first

class PlaybackViewModel @ViewModelInject constructor(
    private val recordings: Recordings,
    private val callPlayback: CallPlayback
) : ViewModel() {

    val playbackState: StateFlow<PlaybackState> = callPlayback.playbackState

    fun startPlayback(recordingId: Long) = viewModelScope.launchUnit {

        val recording = recordings.getRecording(recordingId).first()
        val playbackStatus = callPlayback.playbackState.first()

        with(callPlayback) {
            when {
                playbackStatus is PlaybackState.NotStopped.Paused && playbackStatus.recording.id == recording.id -> {
                    playbackStatus.resumePlayback()
                }
                else -> playbackStatus.startNewPlayback(recording)
            }
        }
    }

    fun pausePlayback() = viewModelScope.launchUnit {
        val playbackStatus = callPlayback.playbackState.first()

        with(callPlayback) {
            (playbackStatus as? PlaybackState.NotStopped.Playing)?.pausePlayback()
        }
    }

    fun setPlaybackPosition(position: Float) = viewModelScope.launchUnit {
        val playbackStatus = callPlayback.playbackState.first()

        with(callPlayback) {
            (playbackStatus as? PlaybackState.NotStopped)?.setPosition(position)
        }
    }

    private fun stopPlayback() = viewModelScope.launchUnit {
        val playbackStatus = callPlayback.playbackState.first()

        with(callPlayback) {
            (playbackStatus as? PlaybackState.NotStopped)?.stopPlayback()
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopPlayback()
    }
}
