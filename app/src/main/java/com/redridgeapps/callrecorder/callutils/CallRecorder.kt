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
import com.redridgeapps.callrecorder.RecordingQueries
import com.redridgeapps.callrecorder.callutils.recorder.Recorder
import com.redridgeapps.callrecorder.di.modules.android.PerService
import com.redridgeapps.callrecorder.utils.CallLogFetcher
import com.redridgeapps.callrecorder.utils.PREF_RECORDING_API
import com.redridgeapps.callrecorder.utils.ToastMaker
import com.redridgeapps.callrecorder.utils.get
import java.io.File
import java.time.Instant
import javax.inject.Inject

@PerService
class CallRecorder @Inject constructor(
    private val context: Context,
    private val prefs: SharedPreferences,
    private val lifecycle: Lifecycle,
    private val toastMaker: ToastMaker,
    private val callLogFetcher: CallLogFetcher,
    private val recordingQueries: RecordingQueries
) {

    private val am = context.getSystemService<AudioManager>()!!
    private lateinit var recordingAPI: RecordingAPI
    private lateinit var saveDir: File
    private var recorder: Recorder? = null
    private var currentStreamVolume = -1

    private lateinit var callStartTime: Instant
    private lateinit var callEndTime: Instant

    private val observer = object : DefaultLifecycleObserver {
        override fun onStop(owner: LifecycleOwner) {
            releaseRecorder()
        }
    }

    private fun setup() {

        val recordingAPIStr = prefs.get(PREF_RECORDING_API)
        recordingAPI = RecordingAPI.valueOf(recordingAPIStr)

        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED)
            error("External storage is not writable")

        val externalStorageVolumes = ContextCompat.getExternalFilesDirs(context, null)
        val primaryExternalStorage = externalStorageVolumes[0]
        saveDir = File(primaryExternalStorage, "CallRecordings")

        if (!saveDir.exists())
            saveDir.mkdir()
    }

    fun startRecording() {

        setup()

        recorder = recordingAPI.init(saveDir, prefs)

        val fileName = Instant.now().epochSecond
        recorder!!.startRecording(fileName.toString())

        maximizeVolume()

        lifecycle.addObserver(observer)

        toastMaker.newToast("Started recording").show()

        callStartTime = Instant.now()
    }

    fun stopRecording() {
        val savePath = recorder!!.stopRecording()

        restoreVolume()

        lifecycle.removeObserver(observer)

        toastMaker.newToast("Stopped recording").show()

        callEndTime = Instant.now()

        insertIntoDatabase(savePath)
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

    private fun insertIntoDatabase(savePath: String) {

        val callEntry = callLogFetcher.getLastCallEntry() ?: error("No call log Found!")

        recordingQueries.insert(
            name = callEntry.name,
            number = callEntry.number,
            startTime = callStartTime.toEpochMilli(),
            endTime = callEndTime.toEpochMilli(),
            callType = callEntry.type,
            savePath = savePath
        )
    }
}
