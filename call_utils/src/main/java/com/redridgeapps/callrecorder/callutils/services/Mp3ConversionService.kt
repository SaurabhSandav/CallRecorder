package com.redridgeapps.callrecorder.callutils.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.redridgeapps.callrecorder.callutils.R
import com.redridgeapps.callrecorder.callutils.storage.Recordings
import com.redridgeapps.callrecorder.common.constants.NOTIFICATION_MP3_CONVERSION_FINISHED_ID
import com.redridgeapps.callrecorder.common.constants.NOTIFICATION_MP3_CONVERSION_ONGOING_ID
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class Mp3ConversionService : LifecycleService() {

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var recordings: Recordings

    private var totalJobCount = 0
    private var ongoingJobCount = 0

    private val conversionActor = lifecycleScope.actor<Long>(start = CoroutineStart.LAZY) {

        channel.invokeOnClose {
            stopService(applicationContext)
            showFinishedNotification()
        }

        for (recordingId in channel) {

            ongoingJobCount++
            showOngoingNotification()

            recordings.convertToMp3(recordingId)

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
            recordingIdList.forEach { conversionActor.send(it) }
        }

        return START_STICKY
    }

    private fun showOngoingNotification() {

        val channel = NotificationChannel(
            Mp3ConversionService::class.simpleName,
            "Mp3 Conversion",
            NotificationManager.IMPORTANCE_LOW
        ).apply { lockscreenVisibility = Notification.VISIBILITY_SECRET }

        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, channel.id)
            .setContentText("Converting to Mp3 ($ongoingJobCount/$totalJobCount)")
            .setSmallIcon(R.drawable.ic_stat_name)
            .setShowWhen(false)
            .build()

        startForeground(NOTIFICATION_MP3_CONVERSION_ONGOING_ID, notification)
    }

    private fun showFinishedNotification() {

        val channel = NotificationChannel(
            Mp3ConversionService::class.simpleName,
            "Mp3 Conversion",
            NotificationManager.IMPORTANCE_LOW
        ).apply { lockscreenVisibility = Notification.VISIBILITY_SECRET }

        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(this, channel.id)
            .setContentText("Conversion to Mp3 finished")
            .setSmallIcon(R.drawable.ic_stat_name)
            .build()

        notificationManager.notify(NOTIFICATION_MP3_CONVERSION_FINISHED_ID, notification)
    }

    companion object {

        private const val ACTION_STOP = "ACTION_STOP"
        private const val EXTRA_RECORDING_ID = "EXTRA_RECORDING_ID"

        fun start(context: Context, recordingIdList: List<Long>) {

            val recordingIdArray = recordingIdList.toLongArray()

            val intent = Intent(context, Mp3ConversionService::class.java).apply {
                putExtra(EXTRA_RECORDING_ID, recordingIdArray)
            }

            ContextCompat.startForegroundService(
                context,
                intent
            )
        }

        private fun stopService(context: Context) {

            val intent = Intent(context, Mp3ConversionService::class.java)
            intent.action = ACTION_STOP

            ContextCompat.startForegroundService(context, intent)
        }
    }
}

class Mp3ConversionServiceLauncher @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun launch(recordingIdList: List<Long>) {
        Mp3ConversionService.start(context, recordingIdList)
    }
}
