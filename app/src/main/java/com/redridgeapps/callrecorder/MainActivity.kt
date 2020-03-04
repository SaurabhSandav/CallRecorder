package com.redridgeapps.callrecorder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.ui.core.setContent
import com.koduok.compose.navigation.core.backStackController
import com.redridgeapps.repository.ISystemizer
import com.redridgeapps.ui.Root
import com.redridgeapps.ui.initialization.UIInitializer
import com.redridgeapps.ui.utils.UIInitializersAmbient
import com.redridgeapps.ui.utils.WithAmbients
import dagger.android.AndroidInjection
import javax.inject.Inject
import javax.inject.Provider

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var iSystemizer: ISystemizer

    @Inject
    lateinit var uiInitializers: Map<Class<out UIInitializer>, @JvmSuppressWildcards Provider<UIInitializer>>

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        setContent {
            val content = @Composable() {
                Root(iSystemizer)
            }

            WithAmbients(
                UIInitializersAmbient provides uiInitializers,
                content = content
            )
        }
    }

    override fun onBackPressed() {
        if (!backStackController.pop())
            super.onBackPressed()
    }
}
