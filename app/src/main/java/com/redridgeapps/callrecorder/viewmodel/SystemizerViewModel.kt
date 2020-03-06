package com.redridgeapps.callrecorder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redridgeapps.callrecorder.utils.Systemizer
import com.redridgeapps.repository.viewmodel.ISystemizerViewModel
import com.redridgeapps.ui.SystemizerUIModel
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
                model.isAppSystemized = it
                model.refreshing = true
            }
            .launchIn(viewModelScope)
    }

    override val model = SystemizerUIModel()

    override fun systemize() {
        viewModelScope.launch {
            model.refreshing = false
            systemizer.systemize()
        }
    }

    override fun unSystemize() {
        viewModelScope.launch {
            model.refreshing = false
            systemizer.unSystemize()
        }
    }
}
