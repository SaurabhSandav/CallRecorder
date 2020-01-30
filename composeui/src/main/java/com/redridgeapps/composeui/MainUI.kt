package com.redridgeapps.composeui

import androidx.compose.Composable
import androidx.compose.state
import androidx.ui.core.Text
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.material.*
import androidx.ui.text.TextStyle
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.dp
import androidx.ui.unit.sp

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
            RecordButton()
            Spacer(modifier = LayoutHeight(100.dp))
            PlaybackButton()
        }
    }
}

@Composable
fun RecordButton() {

    var recording by state { false }
    val buttonStyle = when (recording) {
        true -> ContainedButtonStyle(backgroundColor = Color.Red)
        false -> ContainedButtonStyle()
    }

    Button(
        modifier = LayoutSize(300.dp, 200.dp),
        onClick = { recording = !recording },
        style = buttonStyle
    ) {
        val buttonTitle = when (recording) {
            true -> "Stop Recording"
            false -> "Start Recording"
        }

        Text(
            text = buttonTitle,
            style = TextStyle(fontSize = 24.sp)
        )
    }
}

@Composable
fun PlaybackButton() {

    var playing by state { false }
    val buttonStyle = when (playing) {
        true -> ContainedButtonStyle(backgroundColor = Color.Red)
        false -> ContainedButtonStyle()
    }

    Button(
        modifier = LayoutSize(300.dp, 200.dp),
        onClick = { playing = !playing },
        style = buttonStyle
    ) {
        val buttonTitle = when (playing) {
            true -> "Stop Playback"
            false -> "Start Playback"
        }

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
