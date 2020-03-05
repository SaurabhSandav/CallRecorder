package com.redridgeapps.callrecorder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redridgeapps.callrecorder.utils.Systemizer
import com.redridgeapps.repository.uimodel.ISystemizerUIModel
import com.redridgeapps.repository.viewmodel.ISystemizerViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class SystemizerViewModel @Inject constructor(
    private val systemizer: Systemizer
) : ViewModel(), ISystemizerViewModel {

    private lateinit var model: ISystemizerUIModel

    override fun setModel(newModel: ISystemizerUIModel) {

        model = newModel

        systemizer.isAppSystemizedFlow
            .onEach {
                model.isAppSystemized = it
                model.isInitialized = true
            }
            .launchIn(viewModelScope)
    }

    override fun systemize() {
        viewModelScope.launch {
            model.isInitialized = false
            systemizer.systemize()
        }
    }

    override fun unSystemize() {
        viewModelScope.launch {
            model.isInitialized = false
            systemizer.unSystemize()
        }
    }
}
