package com.redridgeapps.callrecorder.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.redridgeapps.callrecorder.MainActivity
import com.redridgeapps.callrecorder.R
import com.redridgeapps.callrecorder.callutils.*
import com.redridgeapps.callrecorder.utils.constants.NOTIFICATION_RECORDING_SERVICE_ID
import com.redridgeapps.callrecorder.utils.prefs.Prefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CallingService : LifecycleService() {

    @Inject
    lateinit var prefs: Prefs

    @Inject
    lateinit var telephonyManager: TelephonyManager

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var callRecorder: CallRecorder

    private val callStatusListener = CallStatusListener()
    private val audioWriter = AudioWriter(lifecycleScope)

    override fun onCreate() {
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

        telephonyManager.listen(callStatusListener, PhoneStateListener.LISTEN_CALL_STATE)
        observeCallStatusForRecording()

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        telephonyManager.listen(callStatusListener, PhoneStateListener.LISTEN_NONE)

        lifecycleScope.launch {
            (callRecorder.recordingState.first() as? RecordingState.IsRecording)?.stopRecording()
        }
    }

    private fun createNotification() {

        val channel = NotificationChannel(
            CallingService::class.simpleName,
            "Call Recorder",
            NotificationManager.IMPORTANCE_LOW
        ).apply { lockscreenVisibility = Notification.VISIBILITY_SECRET }

        notificationManager.createNotificationChannel(channel)

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val notification = NotificationCompat.Builder(this, channel.id)
            .setContentText("Standing by to record...")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setShowWhen(false)
            .build()

        startForeground(NOTIFICATION_RECORDING_SERVICE_ID, notification)
    }

    private fun observeCallStatusForRecording() {
        callStatusListener.callEventStatus
            .onEach { it.handleCallStatusChange() }
            .launchIn(lifecycleScope)
    }

    private suspend fun NewCallEvent.handleCallStatusChange() {
        when (callStatus()) {
            CallStatus.STARTED -> {
                val recorder = callRecorder.recordingState.first() as? RecordingState.Idle
                recorder?.startRecording(RecordingJob(prefs, this), audioWriter)
            }
            CallStatus.ENDED -> {
                val recorder = callRecorder.recordingState.first() as? RecordingState.IsRecording
                recorder?.stopRecording()
            }
            else -> Unit
        }
    }

    companion object {

        private const val ACTION_STOP = "ACTION_STOP"

        fun start(context: Context) {

            ContextCompat.startForegroundService(
                context,
                Intent(context, CallingService::class.java)
            )
        }

        fun stop(context: Context) {

            val intent = Intent(context, CallingService::class.java)
            intent.action = ACTION_STOP

            ContextCompat.startForegroundService(context, intent)
        }
    }
}
