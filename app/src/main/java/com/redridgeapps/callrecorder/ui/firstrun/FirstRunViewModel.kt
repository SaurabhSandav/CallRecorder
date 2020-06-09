package com.redridgeapps.callrecorder.ui.firstrun

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redridgeapps.callrecorder.prefs.MyPrefs
import com.redridgeapps.callrecorder.prefs.Prefs
import com.redridgeapps.callrecorder.utils.Systemizer
import com.redridgeapps.callrecorder.utils.launchUnit

class FirstRunViewModel @ViewModelInject constructor(
    private val systemizer: Systemizer,
    private val prefs: Prefs
) : ViewModel() {

    val uiState = FirstRunState(systemizer.isAppSystemizedFlow)

    fun systemize() = viewModelScope.launchUnit {
        systemizer.systemize()
    }

    fun configurationFinished() = viewModelScope.launchUnit {
        prefs.set(MyPrefs.IS_FIRST_RUN, false)
        prefs.set(MyPrefs.IS_RECORDING_ON, true)
    }
}
