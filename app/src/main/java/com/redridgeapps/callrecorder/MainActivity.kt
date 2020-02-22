package com.redridgeapps.callrecorder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.Providers
import androidx.ui.core.setContent
import com.redridgeapps.callrecorder.callutils.*
import com.redridgeapps.callrecorder.services.CallingService
import com.redridgeapps.callrecorder.ui.MainUI

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CallingService.startSurveillance(this@MainActivity)

        setContent {
            val content = @Composable() {
                MainUI(getString(R.string.app_name))
            }

            WithAmbients(content)
        }
    }

    @Composable
    private fun WithAmbients(content: @Composable() () -> Unit) {
        Providers(
            PermissionsAmbient provides RuntimePermissions(this@MainActivity),
            CallRecorderAmbient provides CallRecorder(
                RecordingAPI.AudioRecord,
                application,
                lifecycle
            ),
            CallPlaybackAmbient provides CallPlayback(this@MainActivity),
            children = content
        )
    }
}
