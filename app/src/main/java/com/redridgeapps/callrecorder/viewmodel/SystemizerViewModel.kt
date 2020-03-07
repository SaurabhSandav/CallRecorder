package com.redridgeapps.callrecorder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redridgeapps.callrecorder.utils.Systemizer
import com.redridgeapps.repository.viewmodel.ISystemizerViewModel
import com.redridgeapps.ui.SystemizerState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class SystemizerViewModel @Inject constructor(
    private val systemizer: Systemizer
) : ViewModel(), ISystemizerViewModel {

    init {

        systemizer.isAppSystemizedFlow
            .onEach {
                uiState.isAppSystemized = it
                uiState.refreshing = true
            }
            .launchIn(viewModelScope)
    }

    override val uiState = SystemizerState()

    override fun systemize() {
        viewModelScope.launch {
            uiState.refreshing = false
            systemizer.systemize()
        }
    }

    override fun unSystemize() {
        viewModelScope.launch {
            uiState.refreshing = false
            systemizer.unSystemize()
        }
    }
}
