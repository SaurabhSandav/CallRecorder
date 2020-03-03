package com.redridgeapps.callrecorder.callutils

import android.content.Context
import android.content.SharedPreferences
import android.media.AudioManager
import android.os.Environment
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.redridgeapps.callrecorder.callutils.recorder.Recorder
import com.redridgeapps.callrecorder.di.modules.android.PerService
import com.redridgeapps.callrecorder.utils.PREF_RECORDING_API
import java.io.File
import javax.inject.Inject

@PerService
class CallRecorder @Inject constructor(
    private val context: Context,
    private val prefs: SharedPreferences,
    private val lifecycle: Lifecycle
) {

    private val am = context.getSystemService<AudioManager>()!!
    private lateinit var recordingAPI: RecordingAPI
    private lateinit var saveDir: File
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
        saveDir = File(primaryExternalStorage, "CallRecordings")

        if (saveDir.exists())
            saveDir.mkdir()
    }

    fun startRecording() {

        setup()

        recorder = recordingAPI.init(saveDir)
        recorder!!.startRecording()

        maximizeVolume()

        lifecycle.addObserver(observer)

        Toast.makeText(context, "Started recording", Toast.LENGTH_LONG).show()
    }

    fun stopRecording() {
        recorder!!.stopRecording()

        restoreVolume()

        lifecycle.removeObserver(observer)

        Toast.makeText(context, "Stopped recording", Toast.LENGTH_LONG).show()
    }

    fun releaseRecorder() {
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
