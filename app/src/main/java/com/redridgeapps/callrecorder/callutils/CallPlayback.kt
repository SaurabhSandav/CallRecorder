package com.redridgeapps.callrecorder.callutils

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.staticAmbientOf
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

val CallPlaybackAmbient = staticAmbientOf<CallPlayback>()

class CallPlayback(private val activity: AppCompatActivity) {

    private val fileName = "${activity.externalCacheDir!!.absolutePath}/audiorecordtest.mp3"
    private var player: MediaPlayer? = null

    fun startPlaying(onComplete: () -> Unit) {
        player = MediaPlayer().apply {
            setDataSource(fileName)
            prepare()
            start()
            setOnCompletionListener { onComplete() }
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
