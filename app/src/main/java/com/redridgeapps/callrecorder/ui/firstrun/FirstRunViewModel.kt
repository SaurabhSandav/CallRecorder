package com.redridgeapps.callrecorder.ui.firstrun

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redridgeapps.callrecorder.utils.Systemizer
import com.redridgeapps.callrecorder.utils.launchNoJob
import com.redridgeapps.callrecorder.utils.prefs.PREF_IS_FIRST_RUN
import com.redridgeapps.callrecorder.utils.prefs.PREF_IS_RECORDING_ON
import com.redridgeapps.callrecorder.utils.prefs.Prefs
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class FirstRunViewModel @Inject constructor(
    private val systemizer: Systemizer,
    private val prefs: Prefs
) : ViewModel() {

    init {

        systemizer.isAppSystemizedFlow
            .onEach { uiState.isAppSystemized = it }
            .launchIn(viewModelScope)
    }

    val uiState = FirstRunState()

    fun systemize() = viewModelScope.launchNoJob {
        uiState.isAppSystemized = null
        systemizer.systemize()
    }

    fun configurationFinished() = viewModelScope.launchNoJob {
        prefs.set(PREF_IS_FIRST_RUN, false)
        prefs.set(PREF_IS_RECORDING_ON, true)
    }
}
