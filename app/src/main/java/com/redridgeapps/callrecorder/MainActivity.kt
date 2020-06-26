package com.redridgeapps.callrecorder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.redridgeapps.callrecorder.callutils.storage.Recordings
import com.redridgeapps.callrecorder.common.utils.launchUnit
import com.redridgeapps.callrecorder.prefs.PREF_IS_FIRST_RUN
import com.redridgeapps.callrecorder.prefs.PREF_RECORDINGS_STORAGE_PATH
import com.redridgeapps.callrecorder.prefs.Prefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
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

        if (prefs.prefStringOrNull(PREF_RECORDINGS_STORAGE_PATH).first() == null) {

            val newRecordingPath =
                Recordings.getRecordingsStoragePath(applicationContext).toString()

            prefs.editor { setString(PREF_RECORDINGS_STORAGE_PATH, newRecordingPath) }
        }
    }

    private fun setupUI() = lifecycleScope.launchUnit {

        val isFirstRun = prefs.prefBoolean(PREF_IS_FIRST_RUN) { true }.first()
        setupCompose(isFirstRun)

        // Remove Splash Screen
        window.setBackgroundDrawableResource(android.R.color.transparent)
    }
}
