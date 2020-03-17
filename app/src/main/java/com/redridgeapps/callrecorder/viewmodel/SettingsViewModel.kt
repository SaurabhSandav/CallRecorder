package com.redridgeapps.callrecorder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redridgeapps.callrecorder.utils.Systemizer
import com.redridgeapps.callrecorder.utils.prefs.PREF_IS_RECORDING_ON
import com.redridgeapps.callrecorder.utils.prefs.PREF_RECORDING_API
import com.redridgeapps.callrecorder.utils.prefs.Prefs
import com.redridgeapps.callrecorder.utils.prefs.TypedPref
import com.redridgeapps.repository.callutils.RecordingAPI
import com.redridgeapps.repository.viewmodel.ISettingsViewModel
import com.redridgeapps.ui.SettingsState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val prefs: Prefs,
    private val systemizer: Systemizer
) : ViewModel(), ISettingsViewModel {

    override val uiState: SettingsState = SettingsState()

    init {
        viewModelScope.launch {

            observePref(PREF_IS_RECORDING_ON) { uiState.isRecordingOn = it }
            observePref(PREF_RECORDING_API) { uiState.recordingAPI = RecordingAPI.valueOf(it) }

            systemizer.isAppSystemizedFlow
                .onEach { uiState.isSystemized = it }
                .launchIn(viewModelScope)
        }
    }

    override fun flipSystemization() {
        viewModelScope.launch {

            uiState.isSystemized = null

            if (systemizer.isAppSystemizedFlow.first())
                systemizer.unSystemize()
            else
                systemizer.systemize()
        }
    }

    override fun flipRecording() {
        viewModelScope.launch {

            uiState.isRecordingOn = null

            val flippedIsRecording = !prefs.get(PREF_IS_RECORDING_ON).first()
            prefs.set(PREF_IS_RECORDING_ON, flippedIsRecording)
        }
    }

    override fun setRecordingAPI(recordingAPI: RecordingAPI) {
        viewModelScope.launch {

            uiState.recordingAPI = null

            prefs.set(PREF_RECORDING_API, recordingAPI.toString())
        }
    }

    private fun <T> observePref(pref: TypedPref<T>, action: suspend (T) -> Unit) {
        prefs.get(pref)
            .onEach(action)
            .launchIn(viewModelScope)
    }
}
