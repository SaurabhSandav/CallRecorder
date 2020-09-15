package com.redridgeapps.ui.firstrun

import android.Manifest
import androidx.datastore.DataStore
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redridgeapps.common.PermissionChecker
import com.redridgeapps.common.Systemizer
import com.redridgeapps.common.utils.launchUnit
import com.redridgeapps.prefs.Prefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

internal class FirstRunViewModel @ViewModelInject constructor(
    private val systemizer: Systemizer,
    private val prefs: DataStore<Prefs>,
    permissionChecker: PermissionChecker,
) : ViewModel() {

    private val initialState = FirstRunState(
        onAppSystemize = this::onAppSystemize,
        allPermissionsGranted = REQUESTED_DANGEROUS_PERMISSIONS.all {
            permissionChecker.isPermissionGranted(it)
        },
        onPermissionsResult = this::onPermissionResult,
        captureAudioOutputPermissionGranted = permissionChecker
            .isPermissionGranted(Manifest.permission.CAPTURE_AUDIO_OUTPUT),
    )
    private val _uiState = MutableStateFlow(initialState)

    val uiState: StateFlow<FirstRunState> by ::_uiState

    init {

        systemizer.isAppSystemizedFlow
            .onEach { _uiState.value = _uiState.value.copy(isAppSystemized = it) }
            .launchIn(viewModelScope)

        uiState.onEach { checkConfigurationFinished(it) }.launchIn(viewModelScope)
    }

    private fun onAppSystemize() = viewModelScope.launchUnit {
        systemizer.systemize()
    }

    private fun onPermissionResult(allPermissionsGranted: Boolean) {
        _uiState.value = _uiState.value.copy(allPermissionsGranted = allPermissionsGranted)
    }

    private suspend fun checkConfigurationFinished(uiState: FirstRunState) {

        val isConfigFinished = uiState.isAppSystemized
                && uiState.allPermissionsGranted
                && uiState.captureAudioOutputPermissionGranted

        if (isConfigFinished) {

            prefs.updateData {
                it.copy(is_initial_config_finished = true, is_recording_enabled = true)
            }

            _uiState.value = _uiState.value.copy(isConfigFinished = true)
        }
    }
}
