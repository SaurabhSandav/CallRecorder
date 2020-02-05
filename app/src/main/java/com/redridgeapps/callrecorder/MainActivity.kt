package com.redridgeapps.callrecorder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.ui.core.setContent
import com.redridgeapps.callrecorder.callutils.CallPlayback
import com.redridgeapps.callrecorder.callutils.CallPlaybackAmbient
import com.redridgeapps.callrecorder.callutils.CallRecorder
import com.redridgeapps.callrecorder.callutils.CallRecorderAmbient

private typealias AmbientProvider = @Composable() (@Composable() () -> Unit) -> Unit

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ambientProviders = listOf<AmbientProvider>(
            { children ->
                PermissionsAmbient.Provider(RuntimePermissions(this@MainActivity), children)
            }, { children ->
                CallRecorderAmbient.Provider(CallRecorder(this@MainActivity), children)
            }, { children ->
                CallPlaybackAmbient.Provider(CallPlayback(this@MainActivity), children)
            }
        )

        setContent {
            val content = @Composable() {
                MainUI(getString(R.string.app_name))
            }

            ambientProviders.fold(content, { current, ambient ->
                { ambient(current) }
            }).invoke()
        }
        /*setContent {
            PermissionsAmbient.Provider(value = RuntimePermissions(this@MainActivity)) {
                CallRecorderAmbient.Provider(value = CallRecorder(this@MainActivity)) {
                    CallPlaybackAmbient.Provider(value = CallPlayback(this@MainActivity)) {
                        MainUI(getString(R.string.app_name))
                    }
                }
            }
        }*/
    }
}
