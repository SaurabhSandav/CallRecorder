package com.redridgeapps.callrecorder

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.DataStore
import androidx.lifecycle.lifecycleScope
import com.redridgeapps.common.utils.launchUnit
import com.redridgeapps.common.viewmodel.ViewModelAssistedFactoryMap
import com.redridgeapps.prefs.Prefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var prefs: DataStore<Prefs>

    @Inject
    lateinit var viewModelAssistedFactories: ViewModelAssistedFactoryMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupUI()
    }

    private fun setupUI() = lifecycleScope.launchUnit {

        val isFirstRun = prefs.data.first().is_initial_config_finished.not()
        setupCompose(isFirstRun, viewModelAssistedFactories)

        // Remove Splash Screen
        window.setBackgroundDrawableResource(android.R.color.transparent)
    }
}
