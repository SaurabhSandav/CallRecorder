package com.redridgeapps.ui

import androidx.compose.Composable
import androidx.compose.onDispose
import androidx.compose.state
import androidx.ui.core.Modifier
import androidx.ui.core.Text
import androidx.ui.graphics.Color
import androidx.ui.layout.Center
import androidx.ui.layout.Column
import androidx.ui.layout.LayoutSize
import androidx.ui.material.Button
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Scaffold
import androidx.ui.material.TopAppBar
import androidx.ui.res.stringResource
import androidx.ui.text.TextStyle
import androidx.ui.unit.sp
import com.redridgeapps.repository.ICallPlayback
import com.redridgeapps.repository.ICallRecorder
import javax.inject.Inject

class MainUIInitializer @Inject constructor(
    private val callRecorder: ICallRecorder,
    private val callPlayback: ICallPlayback
) : UIInitializer {

    @Composable
    override fun initialize() {
        MainUI(callRecorder, callPlayback)
    }
}

@Composable
fun MainUI(callRecorder: ICallRecorder, callPlayback: ICallPlayback) {

    MaterialTheme {
        val topAppBar = @Composable {
            TopAppBar(title = @Composable { Text(text = stringResource(R.string.app_name)) })
        }
        Scaffold(topAppBar = topAppBar) {
            ContentMain(callRecorder, callPlayback)
        }
    }
}

@Composable
fun ContentMain(callRecorder: ICallRecorder, callPlayback: ICallPlayback) {
    Center {
        Column {
            RecordButton(LayoutFlexible(.5f), callRecorder)
            PlaybackButton(LayoutFlexible(.5f), callPlayback)
        }
    }
}

@Composable
fun RecordButton(modifier: Modifier = Modifier.None, callRecorder: ICallRecorder) {

    var recording by state { false }

    val onClick = {
        recording = !recording

        if (recording) callRecorder.startRecording() else callRecorder.stopRecording()
    }

    onDispose { callRecorder.releaseRecorder() }

    Button(
        modifier = modifier + LayoutSize.Fill,
        onClick = onClick,
        backgroundColor = if (recording) Color.Red else Color.Cyan
    ) {
        val buttonTitle = if (recording) "Stop Recording" else "Start Recording"

        Text(
            text = buttonTitle,
            style = TextStyle(fontSize = 24.sp)
        )
    }
}

@Composable
fun PlaybackButton(modifier: Modifier = Modifier.None, callPlayback: ICallPlayback) {

    var playing by state { false }

    val onClick = {
        playing = !playing

        val onComplete = { playing = false }
        if (playing) callPlayback.startPlaying(onComplete) else callPlayback.stopPlaying()
    }

    Button(
        modifier = modifier + LayoutSize.Fill,
        onClick = onClick,
        backgroundColor = if (playing) Color.Red else Color.Green
    ) {
        val buttonTitle = if (playing) "Stop Playback" else "Start Playback"

        Text(
            text = buttonTitle,
            style = TextStyle(fontSize = 24.sp)
        )
    }
}
