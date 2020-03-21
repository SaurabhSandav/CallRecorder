package com.redridgeapps.callrecorder

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.redridgeapps.callrecorder.utils.prefs.PREF_IS_FIRST_RUN
import com.redridgeapps.callrecorder.utils.prefs.Prefs
import com.redridgeapps.callrecorder.viewmodel.ComposeViewModelStores
import com.redridgeapps.callrecorder.viewmodel.utils.ComposeViewModelFetcherFactory
import com.redridgeapps.ui.destroyUI
import com.redridgeapps.ui.routing.composeHandleBackPressed
import com.redridgeapps.ui.showUI
import dagger.android.AndroidInjection
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var prefs: Prefs

    @Inject
    lateinit var composeViewModelFetcherFactory: ComposeViewModelFetcherFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        setupCompose()

        // Remove Splash Screen
        window.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyUI()
    }

    override fun onBackPressed() {
        if (!composeHandleBackPressed())
            super.onBackPressed()
    }

    private fun setupCompose() = lifecycleScope.launch {

        val composeViewModelStores by viewModels<ComposeViewModelStores>()
        val composeViewModelFetcher = composeViewModelFetcherFactory.create(composeViewModelStores)
        val isFirstRun = prefs.get(PREF_IS_FIRST_RUN)

        showUI(
            isFirstRun,
            lifecycle,
            activityResultRegistry,
            composeViewModelStores,
            composeViewModelFetcher
        )
    }
}
