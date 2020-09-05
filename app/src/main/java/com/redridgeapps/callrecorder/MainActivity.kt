package com.redridgeapps.callrecorder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.redridgeapps.common.utils.launchUnit
import com.redridgeapps.prefs.PREF_IS_FIRST_RUN
import com.redridgeapps.prefs.Prefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var prefs: Prefs

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupUI()
    }

    private fun setupUI() = lifecycleScope.launchUnit {

        val isFirstRun = prefs.boolean(PREF_IS_FIRST_RUN) { true }.first()
        setupCompose(isFirstRun)

        // Remove Splash Screen
        window.setBackgroundDrawableResource(android.R.color.transparent)
    }
}
