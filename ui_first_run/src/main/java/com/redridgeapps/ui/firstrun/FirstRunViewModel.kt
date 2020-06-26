package com.redridgeapps.ui.firstrun

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redridgeapps.callrecorder.common.Systemizer
import com.redridgeapps.callrecorder.common.utils.launchUnit
import com.redridgeapps.callrecorder.prefs.PREF_IS_FIRST_RUN
import com.redridgeapps.callrecorder.prefs.PREF_RECORDING_ENABLED
import com.redridgeapps.callrecorder.prefs.Prefs

class FirstRunViewModel @ViewModelInject constructor(
    private val systemizer: Systemizer,
    private val prefs: Prefs
) : ViewModel() {

    val uiState = FirstRunState(systemizer.isAppSystemizedFlow)

    fun systemize() = viewModelScope.launchUnit {
        systemizer.systemize()
    }

    fun configurationFinished() = viewModelScope.launchUnit {
        prefs.editor {
            setBoolean(PREF_IS_FIRST_RUN, false)
            setBoolean(PREF_RECORDING_ENABLED, true)
        }
    }
}
