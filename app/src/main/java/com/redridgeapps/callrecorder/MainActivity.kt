package com.redridgeapps.callrecorder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.redridgeapps.callrecorder.callutils.storage.Recordings
import com.redridgeapps.callrecorder.prefs.MyPrefs
import com.redridgeapps.callrecorder.prefs.Prefs
import com.redridgeapps.callrecorder.ui.root.setupCompose
import com.redridgeapps.callrecorder.utils.launchUnit
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var prefs: Prefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupRecordingsStoragePath()
        setupUI()
    }

    private fun setupRecordingsStoragePath() = lifecycleScope.launch {

        if (prefs.get(MyPrefs.RECORDINGS_STORAGE_PATH) { "" }.isNotEmpty()) return@launch

        prefs.set(
            pref = MyPrefs.RECORDINGS_STORAGE_PATH,
            newValue = Recordings.getRecordingsStoragePath(applicationContext).toString()
        )
    }

    private fun setupUI() = lifecycleScope.launchUnit {

        val isFirstRun = prefs.get(MyPrefs.IS_FIRST_RUN) { true }
        setupCompose(isFirstRun)

        // Remove Splash Screen
        window.setBackgroundDrawableResource(android.R.color.transparent)
    }
}
