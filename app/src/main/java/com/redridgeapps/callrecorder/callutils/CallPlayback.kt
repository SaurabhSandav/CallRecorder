package com.redridgeapps.callrecorder.callutils

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Ambient
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

val CallPlaybackAmbient = Ambient.of<CallPlayback>()

class CallPlayback(private val activity: AppCompatActivity) {

    private val fileName = "${activity.externalCacheDir!!.absolutePath}/audiorecordtest.3gp"
    private var player: MediaPlayer? = null

    fun startPlaying() {
        player = MediaPlayer().apply {
            setDataSource(fileName)
            prepare()
            start()
        }

        activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStop(owner: LifecycleOwner) {
                player?.release()
                player = null
            }
        })
    }

    fun stopPlaying() {
        player?.release()
        player = null
    }
}
