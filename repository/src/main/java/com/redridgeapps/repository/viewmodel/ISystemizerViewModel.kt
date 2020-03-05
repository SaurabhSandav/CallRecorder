package com.redridgeapps.repository.viewmodel

import com.redridgeapps.repository.ILiveData

interface ISystemizerViewModel : ViewModelMarker {

    val isAppSystemized: ILiveData<Boolean>

    fun systemize(onComplete: () -> Unit)

    fun unSystemize(onComplete: () -> Unit)
}
