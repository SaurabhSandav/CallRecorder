package com.redridgeapps.repository

interface ICallPlayback {

    fun startPlaying(onComplete: () -> Unit)

    fun stopPlaying()
}
