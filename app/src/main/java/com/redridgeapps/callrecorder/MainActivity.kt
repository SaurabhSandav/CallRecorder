package com.redridgeapps.callrecorder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.ui.core.setContent
import com.redridgeapps.callrecorder.callutils.CallPlayback
import com.redridgeapps.callrecorder.callutils.CallRecorder
import com.redridgeapps.callrecorder.callutils.RecordingAPI
import com.redridgeapps.ui.CallPlaybackAmbient
import com.redridgeapps.ui.CallRecorderAmbient
import com.redridgeapps.ui.MainUI
import com.redridgeapps.ui.WithAmbients

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val content = @Composable() {
                MainUI(getString(R.string.app_name))
            }

            WithAmbients(
                CallRecorderAmbient provides CallRecorder(
                    RecordingAPI.AudioRecord,
                    application,
                    lifecycle
                ),
                CallPlaybackAmbient provides CallPlayback(this),
                content = content
            )
        }
    }
}
