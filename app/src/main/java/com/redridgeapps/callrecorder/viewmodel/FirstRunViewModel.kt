package com.redridgeapps.callrecorder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redridgeapps.callrecorder.utils.Systemizer
import com.redridgeapps.callrecorder.utils.prefs.PREF_IS_FIRST_RUN
import com.redridgeapps.callrecorder.utils.prefs.PREF_IS_RECORDING_ON
import com.redridgeapps.callrecorder.utils.prefs.Prefs
import com.redridgeapps.repository.viewmodel.IFirstRunViewModel
import com.redridgeapps.ui.FirstRunState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class FirstRunViewModel @Inject constructor(
    private val systemizer: Systemizer,
    private val prefs: Prefs
) : ViewModel(), IFirstRunViewModel {

    init {

        systemizer.isAppSystemizedFlow
            .onEach { uiState.isAppSystemized = it }
            .launchIn(viewModelScope)
    }

    override val uiState = FirstRunState()

    override fun systemize() {
        viewModelScope.launch {
            uiState.isAppSystemized = null
            systemizer.systemize()
        }
    }

    override fun configurationFinished() {
        viewModelScope.launch {
            prefs.set(PREF_IS_FIRST_RUN, false)
            prefs.set(PREF_IS_RECORDING_ON, true)
        }
    }
}
