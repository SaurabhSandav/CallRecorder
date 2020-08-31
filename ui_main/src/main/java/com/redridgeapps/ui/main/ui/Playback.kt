package com.redridgeapps.ui.main.ui

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.BottomAppBar
import androidx.compose.material.DismissValue
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Slider
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PauseCircleOutline
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.redridgeapps.ui.main.CurrentPlayback
import kotlinx.coroutines.flow.Flow

@Composable
internal fun PlaybackBottomBar(currentPlayback: CurrentPlayback) {

    BottomAppBar {
        SwipeToDismissPlaybackBar(
            title = currentPlayback.title,
            isPlaying = currentPlayback.isPlaying,
            positionFlow = currentPlayback.positionFlow,
            onPlayPauseToggle = currentPlayback.onPlayPauseToggle,
            onPlaybackStop = currentPlayback.onPlaybackStop,
            onPlaybackSeek = currentPlayback.onPlaybackSeek,
        )
    }
}

@Composable
private fun SwipeToDismissPlaybackBar(
    title: String,
    isPlaying: Boolean,
    positionFlow: Flow<Float>,
    onPlayPauseToggle: () -> Unit,
    onPlaybackStop: () -> Unit,
    onPlaybackSeek: (Float) -> Unit,
) {

    val dismissState = rememberDismissState(
        confirmStateChange = {
            if (it !== DismissValue.Default) onPlaybackStop()
            true
        }
    )

    SwipeToDismiss(
        state = dismissState,
        background = {},
    ) {
        PlaybackBar(
            title = title,
            isPlaying = isPlaying,
            playbackPositionFlow = positionFlow,
            onPlayPauseToggle = onPlayPauseToggle,
            onPlaybackSeek = onPlaybackSeek
        )
    }
}

@Composable
private fun PlaybackBar(
    title: String,
    isPlaying: Boolean,
    playbackPositionFlow: Flow<Float>,
    onPlayPauseToggle: () -> Unit,
    onPlaybackSeek: (Float) -> Unit,

    modifier: Modifier = Modifier,
) {
    Row(modifier) {

        Column(Modifier.weight(1F)) {

            Text(
                text = title,
                modifier = Modifier.fillMaxWidth()
                    .wrapContentSize(Alignment.Center)
                    .padding(5.dp),
                color = MaterialTheme.colors.onPrimary
            )

            var sliderPosition by remember { mutableStateOf(0F) }
            val playbackPosition by playbackPositionFlow.collectAsState(sliderPosition)

            Slider(
                value = playbackPosition,
                onValueChange = { sliderPosition = it },
                onValueChangeEnd = { onPlaybackSeek(sliderPosition) },
                thumbColor = MaterialTheme.colors.secondary,
                activeTrackColor = MaterialTheme.colors.secondaryVariant,
                modifier = Modifier.fillMaxWidth()
                    .wrapContentSize(Alignment.Center)
                    .padding(5.dp),
            )
        }

        PlayPauseToggleButton(
            isPlaying = isPlaying,
            onClick = onPlayPauseToggle,
            iconSideSize = 40.dp,
            modifier = Modifier.fillMaxHeight()
                .wrapContentHeight(Alignment.CenterVertically)
                .padding(10.dp)
        )
    }
}

@Composable
internal fun PlayPauseToggleButton(
    isPlaying: Boolean,
    onClick: () -> Unit,
    iconSideSize: Dp,
    modifier: Modifier = Modifier,
) {

    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {

        val icon = when {
            isPlaying -> Icons.Default.PauseCircleOutline
            else -> Icons.Default.PlayCircleOutline
        }

        Icon(
            asset = icon.copy(defaultWidth = iconSideSize, defaultHeight = iconSideSize),
            tint = MaterialTheme.colors.secondary
        )
    }
}
