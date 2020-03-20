package com.redridgeapps.repository.viewmodel

interface IFirstRunViewModel : ViewModelMarker {

    val uiState: Any

    fun systemize()

    fun configurationFinished()
}
