package com.redridgeapps.callrecorder

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.ui.core.setContent
import com.github.zsoltk.compose.backpress.AmbientBackPressHandler
import com.github.zsoltk.compose.backpress.BackPressHandler
import com.redridgeapps.callrecorder.callutils.CallRecorder
import com.redridgeapps.ui.SplashUIInitializer
import com.redridgeapps.ui.UIInitializer
import com.redridgeapps.ui.UIInitializersAmbient
import com.redridgeapps.ui.WithAmbients
import dagger.android.AndroidInjection
import javax.inject.Inject
import javax.inject.Provider

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var prefs: SharedPreferences

    @Inject
    lateinit var callRecorder: CallRecorder

    @Inject
    lateinit var uiInitializers: Map<Class<out UIInitializer>, @JvmSuppressWildcards Provider<UIInitializer>>

    private val backPressHandler = BackPressHandler()

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        setContent {
            val content = @Composable() {
                UIInitializer.get(SplashUIInitializer::class).initialize()
            }

            WithAmbients(
                AmbientBackPressHandler provides backPressHandler,
                UIInitializersAmbient provides uiInitializers,
                content = content
            )
        }
    }

    override fun onBackPressed() {
        if (!backPressHandler.handle()) {
            super.onBackPressed()
        }
    }
}
