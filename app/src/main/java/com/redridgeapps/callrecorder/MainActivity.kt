package com.redridgeapps.callrecorder

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.redridgeapps.callrecorder.callutils.Recordings
import com.redridgeapps.callrecorder.ui.compose_viewmodel.ComposeViewModelFetcherFactory
import com.redridgeapps.callrecorder.ui.compose_viewmodel.ComposeViewModelStores
import com.redridgeapps.callrecorder.ui.root.showUI
import com.redridgeapps.callrecorder.ui.routing.composeHandleBackPressed
import com.redridgeapps.callrecorder.utils.prefs.PREF_IS_FIRST_RUN
import com.redridgeapps.callrecorder.utils.prefs.PREF_RECORDING_PATH
import com.redridgeapps.callrecorder.utils.prefs.Prefs
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

        setRecordingPath()

        setupCompose()

        // Remove Splash Screen
        window.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onBackPressed() {
        if (!composeHandleBackPressed())
            super.onBackPressed()
    }

    private fun setRecordingPath() = lifecycleScope.launch {

        if (prefs.get(PREF_RECORDING_PATH).isNotEmpty()) return@launch

        prefs.set(
            pref = PREF_RECORDING_PATH,
            newValue = Recordings.getRecordingStoragePath(applicationContext).toString()
        )
    }

    private fun setupCompose() = lifecycleScope.launch {

        val composeViewModelStores by viewModels<ComposeViewModelStores>()
        val composeViewModelFetcher = composeViewModelFetcherFactory.create(composeViewModelStores)
        val isFirstRun = prefs.get(PREF_IS_FIRST_RUN)

        showUI(
            isFirstRun,
            activityResultRegistry,
            composeViewModelStores,
            composeViewModelFetcher
        )
    }
}
