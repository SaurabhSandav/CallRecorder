package com.redridgeapps.callrecorder

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.ui.core.setContent
import com.redridgeapps.callrecorder.callutils.CallPlayback
import com.redridgeapps.callrecorder.callutils.CallRecorder
import com.redridgeapps.ui.CallPlaybackAmbient
import com.redridgeapps.ui.CallRecorderAmbient
import com.redridgeapps.ui.MainUI
import com.redridgeapps.ui.WithAmbients
import dagger.android.AndroidInjection
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var prefs: SharedPreferences

    @Inject
    lateinit var callRecorder: CallRecorder

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        setContent {
            val content = @Composable() {
                MainUI("")
            }

            WithAmbients(
                CallRecorderAmbient provides callRecorder,
                CallPlaybackAmbient provides CallPlayback(this),
                content = content
            )
        }
    }
}
