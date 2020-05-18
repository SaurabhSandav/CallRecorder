package com.redridgeapps.callrecorder.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.redridgeapps.callrecorder.R
import com.redridgeapps.callrecorder.callutils.RecordingId
import com.redridgeapps.callrecorder.callutils.Recordings
import com.redridgeapps.callrecorder.utils.NOTIFICATION_WAV_TRIMMING_SERVICE_ID
import dagger.android.AndroidInjection
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.launch
import javax.inject.Inject

class AudioEndsTrimmingService : LifecycleService() {

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var recordings: Recordings

    private val trimmingActor = lifecycleScope.actor<RecordingId>(start = CoroutineStart.LAZY) {

        channel.invokeOnClose { stopService(applicationContext) }

        for (recordingId in channel) {
            recordings.trimSilenceEnds(recordingId)

            if (isEmpty) channel.close()
        }
    }

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()

        createNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        if (intent!!.action == ACTION_STOP) {
            stopForeground(true)
            stopSelf()

            return START_NOT_STICKY
        }

        createNotification()

        val recordingIdList =
            intent.extras!!.getLongArray(EXTRA_RECORDING_ID)!!.map { RecordingId(it) }

        lifecycleScope.launch {
            recordingIdList.forEach { trimmingActor.send(it) }
        }

        return START_STICKY
    }

    private fun createNotification() {

        val channel = NotificationChannel(
            AudioEndsTrimmingService::class.simpleName,
            "Audio Trimming",
            NotificationManager.IMPORTANCE_LOW
        ).apply { lockscreenVisibility = Notification.VISIBILITY_SECRET }

        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, channel.id)
            .setContentText("Trimming audio")
            .setSmallIcon(R.drawable.ic_stat_name)
            .setShowWhen(false)
            .build()

        startForeground(NOTIFICATION_WAV_TRIMMING_SERVICE_ID, notification)
    }

    companion object {

        private const val ACTION_STOP = "ACTION_STOP"
        private const val EXTRA_RECORDING_ID = "EXTRA_RECORDING_ID"

        fun start(context: Context, recordingIdList: List<RecordingId>) {

            val recordingIdArray = recordingIdList.map { it.value }.toLongArray()

            val intent = Intent(context, AudioEndsTrimmingService::class.java).apply {
                putExtra(EXTRA_RECORDING_ID, recordingIdArray)
            }

            ContextCompat.startForegroundService(
                context,
                intent
            )
        }

        private fun stopService(context: Context) {

            val intent = Intent(context, AudioEndsTrimmingService::class.java)
            intent.action = ACTION_STOP

            ContextCompat.startForegroundService(context, intent)
        }
    }
}

class AudioEndsTrimmingServiceLauncher @Inject constructor(private val context: Context) {

    fun launch(recordingIdList: List<RecordingId>) {
        AudioEndsTrimmingService.start(context, recordingIdList)
    }
}