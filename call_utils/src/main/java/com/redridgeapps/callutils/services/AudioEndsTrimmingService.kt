package com.redridgeapps.callutils.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.redridgeapps.callutils.R
import com.redridgeapps.callutils.db.RecordingId
import com.redridgeapps.callutils.storage.Recordings
import com.redridgeapps.common.constants.NOTIFICATION_WAV_TRIMMING_FINISHED_ID
import com.redridgeapps.common.constants.NOTIFICATION_WAV_TRIMMING_ONGOING_ID
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AudioEndsTrimmingService : LifecycleService() {

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var recordings: Recordings

    private var totalJobCount = 0
    private var ongoingJobCount = 0

    private val trimmingActor = lifecycleScope.actor<RecordingId>(start = CoroutineStart.LAZY) {

        channel.invokeOnClose {
            stopService(applicationContext)
            showFinishedNotification()
        }

        for (recordingId in channel) {

            ongoingJobCount++
            showOngoingNotification()

            recordings.trimSilenceEnds(recordingId)

            if (isEmpty) channel.close()
        }
    }

    override fun onCreate() {
        super.onCreate()

        showOngoingNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        if (intent?.action == ACTION_STOP) {
            stopForeground(true)
            stopSelf()

            return START_NOT_STICKY
        }

        val recordingIdList =
            intent?.extras?.getLongArray(EXTRA_RECORDING_ID)?.asList() ?: emptyList()

        totalJobCount += recordingIdList.size
        showOngoingNotification()

        lifecycleScope.launch {
            recordingIdList.map(::RecordingId).forEach { trimmingActor.send(it) }
        }

        return START_STICKY
    }

    private fun showOngoingNotification() {

        val channel = NotificationChannel(
            AudioEndsTrimmingService::class.simpleName,
            "Audio Trimming",
            NotificationManager.IMPORTANCE_LOW
        ).apply { lockscreenVisibility = Notification.VISIBILITY_SECRET }

        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, channel.id)
            .setContentText("Trimming audio ($ongoingJobCount/$totalJobCount)")
            .setSmallIcon(R.drawable.ic_stat_name)
            .setShowWhen(false)
            .build()

        startForeground(NOTIFICATION_WAV_TRIMMING_ONGOING_ID, notification)
    }

    private fun showFinishedNotification() {

        val channel = NotificationChannel(
            AudioEndsTrimmingService::class.simpleName,
            "Audio Trimming",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply { lockscreenVisibility = Notification.VISIBILITY_SECRET }

        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, channel.id)
            .setContentText("Trimming audio finished")
            .setSmallIcon(R.drawable.ic_stat_name)
            .build()

        notificationManager.notify(NOTIFICATION_WAV_TRIMMING_FINISHED_ID, notification)
    }

    companion object {

        private const val ACTION_STOP = "ACTION_STOP"
        private const val EXTRA_RECORDING_ID = "EXTRA_RECORDING_ID"

        fun start(context: Context, recordingIdList: List<RecordingId>) {

            val recordingIdArray = recordingIdList.map { it.value }.toLongArray()

            val intent = Intent(context, AudioEndsTrimmingService::class.java).apply {
                putExtra(EXTRA_RECORDING_ID, recordingIdArray)
            }

            ContextCompat.startForegroundService(context, intent)
        }

        private fun stopService(context: Context) {

            val intent = Intent(context, AudioEndsTrimmingService::class.java)
            intent.action = ACTION_STOP

            ContextCompat.startForegroundService(context, intent)
        }
    }
}

class AudioEndsTrimmingServiceLauncher @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    fun launch(recordingIdList: List<RecordingId>) {
        AudioEndsTrimmingService.start(context, recordingIdList)
    }
}
