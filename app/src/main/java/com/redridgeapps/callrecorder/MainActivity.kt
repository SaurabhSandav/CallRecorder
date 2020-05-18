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
import com.redridgeapps.callrecorder.utils.prefs.MyPrefs
import com.redridgeapps.callrecorder.utils.prefs.Prefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var prefs: Prefs

    @Inject
    lateinit var composeViewModelFetcherFactory: ComposeViewModelFetcherFactory

    override fun onCreate(savedInstanceState: Bundle?) {
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

        if (prefs.get(MyPrefs.RECORDING_PATH) { "" }.isNotEmpty()) return@launch

        prefs.set(
            pref = MyPrefs.RECORDING_PATH,
            newValue = Recordings.getRecordingStoragePath(applicationContext).toString()
        )
    }

    private fun setupCompose() = lifecycleScope.launch {

        val composeViewModelStores by viewModels<ComposeViewModelStores>()
        val composeViewModelFetcher = composeViewModelFetcherFactory.create(composeViewModelStores)
        val isFirstRun = prefs.get(MyPrefs.IS_FIRST_RUN) { true }

        showUI(
            isFirstRun,
            activityResultRegistry,
            composeViewModelStores,
            composeViewModelFetcher
        )
    }
}
