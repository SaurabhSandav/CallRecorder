package com.redridgeapps.callrecorder.callutils

import android.content.Context
import android.content.SharedPreferences
import android.media.AudioManager
import android.os.Environment
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.redridgeapps.callrecorder.callutils.recorder.Recorder
import com.redridgeapps.callrecorder.di.modules.android.PerActivity
import com.redridgeapps.callrecorder.utils.PREF_RECORDING_API
import com.redridgeapps.repository.ICallRecorder
import java.io.File
import javax.inject.Inject

@PerActivity
class CallRecorder @Inject constructor(
    private val context: Context,
    private val prefs: SharedPreferences,
    private val lifecycle: Lifecycle
) : ICallRecorder {

    private val am = context.getSystemService<AudioManager>()!!
    private lateinit var recordingAPI: RecordingAPI
    private lateinit var savePath: File
    private var recorder: Recorder? = null
    private var currentStreamVolume = -1

    private val observer = object : DefaultLifecycleObserver {
        override fun onStop(owner: LifecycleOwner) {
            releaseRecorder()
        }
    }

    private fun setup() {

        val recordingAPIStr =
            prefs.getString(PREF_RECORDING_API, null) ?: RecordingAPI.AudioRecord.toString()
        recordingAPI = RecordingAPI.valueOf(recordingAPIStr)

        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED)
            error("External storage is not writable")

        val externalStorageVolumes = ContextCompat.getExternalFilesDirs(context, null)
        val primaryExternalStorage = externalStorageVolumes[0]
        savePath = File(primaryExternalStorage, "CallRecordings")

        if (savePath.exists())
            savePath.mkdir()
    }

    override fun startRecording() {

        setup()

        recorder = recordingAPI.init(savePath)
        recorder!!.startRecording()

        maximizeVolume()

        lifecycle.addObserver(observer)
    }

    override fun stopRecording() {
        recorder!!.stopRecording()

        restoreVolume()

        lifecycle.removeObserver(observer)
    }

    override fun releaseRecorder() {
        recorder?.releaseRecorder()

        lifecycle.removeObserver(observer)
    }

    private fun maximizeVolume() {
        currentStreamVolume = am.getStreamVolume(AudioManager.STREAM_VOICE_CALL)
        val streamMaxVolume = am.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL)
        am.setStreamVolume(AudioManager.STREAM_VOICE_CALL, streamMaxVolume, 0)
    }

    private fun restoreVolume() {
        am.setStreamVolume(AudioManager.STREAM_VOICE_CALL, currentStreamVolume, 0)
        currentStreamVolume = -1
    }
}
