package com.redridgeapps.callrecorder.ui.firstrun

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redridgeapps.callrecorder.utils.Systemizer
import com.redridgeapps.callrecorder.utils.launchNoJob
import com.redridgeapps.callrecorder.utils.prefs.MyPrefs
import com.redridgeapps.callrecorder.utils.prefs.Prefs
import javax.inject.Inject

class FirstRunViewModel @Inject constructor(
    private val systemizer: Systemizer,
    private val prefs: Prefs
) : ViewModel() {

    val uiState = FirstRunState(systemizer.isAppSystemizedFlow)

    fun systemize() = viewModelScope.launchNoJob {
        systemizer.systemize()
    }

    fun configurationFinished() = viewModelScope.launchNoJob {
        prefs.set(MyPrefs.IS_FIRST_RUN, false)
        prefs.set(MyPrefs.IS_RECORDING_ON, true)
    }
}
