package com.redridgeapps.callrecorder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redridgeapps.callrecorder.utils.Systemizer
import com.redridgeapps.callrecorder.utils.asILiveData
import com.redridgeapps.repository.viewmodel.ISystemizerViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class SystemizerViewModel @Inject constructor(
    private val systemizer: Systemizer
) : ViewModel(), ISystemizerViewModel {

    override val isAppSystemized = systemizer.isAppSystemizedFlow.asILiveData()

    override fun systemize(onComplete: () -> Unit) {
        viewModelScope.launch {
            systemizer.systemize()
            onComplete()
        }
    }

    override fun unSystemize(onComplete: () -> Unit) {
        viewModelScope.launch {
            systemizer.unSystemize()
            onComplete()
        }
    }
}
