package com.redridgeapps.repository.viewmodel

interface IMainViewModel : ViewModelMarker {

    val model: Any

    fun deleteRecording(recordingId: Int)
}
