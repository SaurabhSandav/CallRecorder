package com.redridgeapps.repository.viewmodel

interface ISystemizerViewModel : ViewModelMarker {

    fun isAppSystemized(): Boolean

    fun systemize(onComplete: () -> Unit)

    fun unSystemize(onComplete: () -> Unit)
}
