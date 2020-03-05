package com.redridgeapps.callrecorder

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.ui.core.setContent
import com.koduok.compose.navigation.core.backStackController
import com.redridgeapps.callrecorder.utils.PREF_FIRST_RUN
import com.redridgeapps.callrecorder.utils.get
import com.redridgeapps.callrecorder.viewmodel.utils.ComposeViewModelFetcher
import com.redridgeapps.callrecorder.viewmodel.utils.ComposeViewModelStores
import com.redridgeapps.ui.Root
import com.redridgeapps.ui.initialization.UIInitializer
import com.redridgeapps.ui.utils.ComposeViewModelStoresAmbient
import com.redridgeapps.ui.utils.UIInitializersAmbient
import com.redridgeapps.ui.utils.ViewModelFetcherAmbient
import com.redridgeapps.ui.utils.WithAmbients
import dagger.android.AndroidInjection
import javax.inject.Inject
import javax.inject.Provider

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var prefs: SharedPreferences

    @Inject
    lateinit var uiInitializers: Map<Class<out UIInitializer>, @JvmSuppressWildcards Provider<UIInitializer>>

    @Inject
    lateinit var composeViewModelFetcher: ComposeViewModelFetcher

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        val composeViewModelStores by viewModels<ComposeViewModelStores>()
        val firstRun = prefs.get(PREF_FIRST_RUN)

        setContent {
            val content = @Composable() {
                Root(firstRun)
            }

            WithAmbients(
                UIInitializersAmbient provides uiInitializers,
                ComposeViewModelStoresAmbient provides composeViewModelStores,
                ViewModelFetcherAmbient provides composeViewModelFetcher,
                content = content
            )
        }
    }

    override fun onBackPressed() {
        if (!backStackController.pop())
            super.onBackPressed()
    }
}
