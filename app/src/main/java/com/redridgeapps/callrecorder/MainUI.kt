package com.redridgeapps.callrecorder

import androidx.compose.Composable
import androidx.compose.ambient
import androidx.compose.state
import androidx.ui.core.Modifier
import androidx.ui.core.Text
import androidx.ui.graphics.Color
import androidx.ui.layout.Center
import androidx.ui.layout.Column
import androidx.ui.layout.LayoutSize
import androidx.ui.material.*
import androidx.ui.text.TextStyle
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.sp
import com.redridgeapps.callrecorder.callutils.CallPlaybackAmbient
import com.redridgeapps.callrecorder.callutils.CallRecorderAmbient

@Composable
fun MainUI(appName: String) {

    MaterialTheme {
        val topAppBar = @Composable {
            TopAppBar(title = @Composable { Text(text = appName) })
        }
        Scaffold(topAppBar = topAppBar) {
            ContentMain()
        }
    }
}

@Composable
fun ContentMain() {
    Center {
        Column {
            RecordButton(LayoutFlexible(.5f))
            PlaybackButton(LayoutFlexible(.5f))
        }
    }
}

@Composable
fun RecordButton(modifier: Modifier = Modifier.None) {

    var recording by state { false }
    val callRecorder = ambient(key = CallRecorderAmbient)

    val onClick = {
        recording = !recording

        if (recording) callRecorder.startRecording() else callRecorder.stopRecording()
    }

    Button(
        modifier = modifier + LayoutSize.Fill,
        onClick = onClick,
        style = ContainedButtonStyle(backgroundColor = if (recording) Color.Red else Color.Cyan)
    ) {
        val buttonTitle = if (recording) "Stop Recording" else "Start Recording"

        Text(
            text = buttonTitle,
            style = TextStyle(fontSize = 24.sp)
        )
    }
}

@Composable
fun PlaybackButton(modifier: Modifier = Modifier.None) {

    var playing by state { false }
    val callPlayback = ambient(key = CallPlaybackAmbient)

    val onClick = {
        playing = !playing

        if (playing) callPlayback.startPlaying() else callPlayback.stopPlaying()
    }

    Button(
        modifier = modifier + LayoutSize.Fill,
        onClick = onClick,
        style = ContainedButtonStyle(backgroundColor = if (playing) Color.Red else Color.Green)
    ) {
        val buttonTitle = if (playing) "Stop Playback" else "Start Playback"

        Text(
            text = buttonTitle,
            style = TextStyle(fontSize = 24.sp)
        )
    }
}

@Preview
@Composable
fun DefaultPreview() {
    MainUI("App Title")
}
