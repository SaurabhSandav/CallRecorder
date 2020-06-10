package com.redridgeapps.callrecorder

import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.koduok.compose.navigation.core.backStackController
import com.redridgeapps.callrecorder.callutils.Recordings
import com.redridgeapps.callrecorder.ui.compose_viewmodel.ComposeFramework
import com.redridgeapps.callrecorder.ui.compose_viewmodel.setupViewModel
import com.redridgeapps.callrecorder.ui.root.showUI
import com.redridgeapps.callrecorder.utils.prefs.MyPrefs
import com.redridgeapps.callrecorder.utils.prefs.Prefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var prefs: Prefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setRecordingPath()

        setupCompose()

        // Remove Splash Screen
        window.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun setRecordingPath() = lifecycleScope.launch {

        if (prefs.get(MyPrefs.RECORDING_PATH) { "" }.isNotEmpty()) return@launch

        prefs.set(
            pref = MyPrefs.RECORDING_PATH,
            newValue = Recordings.getRecordingStoragePath(applicationContext).toString()
        )
    }

    private fun setupCompose() = lifecycleScope.launch {

        val composeFramework by viewModels<ComposeFramework>()

        composeFramework.setupViewModel()

        // Handle saving/restoring State
        with(savedStateRegistry) {
            composeFramework.restoreSavedState(consumeRestoredStateForKey(COMPOSE_SAVED_STATE_KEY))
            registerSavedStateProvider(COMPOSE_SAVED_STATE_KEY) {
                Bundle().also { composeFramework.saveState(it) }
            }
        }

        // Handle back pressed
        onBackPressedDispatcher.addCallback(this@MainActivity) {
            if (!backStackController.pop()) {
                isEnabled = false
                this@MainActivity.onBackPressed()
                isEnabled = true
            }
        }

        val composeViewModelFetcher = composeFramework.viewModelFetcher
        val isFirstRun = prefs.get(MyPrefs.IS_FIRST_RUN) { true }

        showUI(isFirstRun, activityResultRegistry, composeViewModelFetcher)
    }
}

const val COMPOSE_SAVED_STATE_KEY = "COMPOSE_SAVED_STATE_KEY"
