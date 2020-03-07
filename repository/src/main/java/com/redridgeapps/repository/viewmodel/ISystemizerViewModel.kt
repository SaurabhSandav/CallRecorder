package com.redridgeapps.repository.viewmodel

interface ISystemizerViewModel : ViewModelMarker {

    val uiState: Any

    fun systemize()

    fun unSystemize()
}
