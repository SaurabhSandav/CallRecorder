package com.redridgeapps.repository.viewmodel

interface ISystemizerViewModel : ViewModelMarker {

    val model: Any

    fun systemize()

    fun unSystemize()
}
