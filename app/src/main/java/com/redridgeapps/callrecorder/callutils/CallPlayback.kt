package com.redridgeapps.callrecorder.callutils

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.redridgeapps.repository.ICallPlayback

class CallPlayback(private val activity: AppCompatActivity) : ICallPlayback {

    private val fileName = "${activity.externalCacheDir!!.absolutePath}/audiorecordtest.mp3"
    private var player: MediaPlayer? = null

    override fun startPlaying(onComplete: () -> Unit) {
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

    override fun stopPlaying() {
        player?.release()
        player = null
    }
}
