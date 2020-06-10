package com.redridgeapps.callrecorder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.redridgeapps.callrecorder.callutils.Recordings
import com.redridgeapps.callrecorder.ui.root.setupCompose
import com.redridgeapps.callrecorder.utils.launchNoJob
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
        setupUI()
    }

    private fun setRecordingPath() = lifecycleScope.launch {

        if (prefs.get(MyPrefs.RECORDING_PATH) { "" }.isNotEmpty()) return@launch

        prefs.set(
            pref = MyPrefs.RECORDING_PATH,
            newValue = Recordings.getRecordingStoragePath(applicationContext).toString()
        )
    }

    private fun setupUI() = lifecycleScope.launchNoJob {

        val isFirstRun = prefs.get(MyPrefs.IS_FIRST_RUN) { true }
        setupCompose(isFirstRun)

        // Remove Splash Screen
        window.setBackgroundDrawableResource(android.R.color.transparent)
    }
}
