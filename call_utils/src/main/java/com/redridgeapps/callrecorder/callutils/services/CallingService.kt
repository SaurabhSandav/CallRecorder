package com.redridgeapps.callrecorder.callutils.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.redridgeapps.callrecorder.callutils.R
import com.redridgeapps.callrecorder.callutils.callevents.CallState
import com.redridgeapps.callrecorder.callutils.callevents.CallStatusListener
import com.redridgeapps.callrecorder.callutils.callevents.NewCallEvent
import com.redridgeapps.callrecorder.callutils.recording.AudioWriter
import com.redridgeapps.callrecorder.callutils.recording.CallRecorder
import com.redridgeapps.callrecorder.callutils.recording.RecordingJob
import com.redridgeapps.callrecorder.callutils.recording.RecordingState
import com.redridgeapps.callrecorder.common.constants.NOTIFICATION_RECORDING_SERVICE_ID
import com.redridgeapps.callrecorder.prefs.Prefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.KClass

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
    private var pendingActivity: Class<out Activity>? = null

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

        getNotificationPendingActivity(intent)
        showOngoingNotification()

        telephonyManager.listen(callStatusListener, PhoneStateListener.LISTEN_CALL_STATE)
        observeCallStatusForRecording()

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()

        telephonyManager.listen(callStatusListener, PhoneStateListener.LISTEN_NONE)

        lifecycleScope.launch {
            with(callRecorder) {
                (recordingState.value as? RecordingState.IsRecording)?.stopRecording()
            }
        }
    }

    private fun getNotificationPendingActivity(intent: Intent?) {

        val newPendingActivity = intent?.getSerializableExtra(EXTRA_NOTIFICATION_PENDING_ACTIVITY)

        requireNotNull(newPendingActivity) { "CallingService: notificationPendingActivity is empty" }

        @Suppress("UNCHECKED_CAST")
        pendingActivity = newPendingActivity as Class<out Activity>?
    }

    private fun showOngoingNotification() {

        val channel = NotificationChannel(
            CallingService::class.simpleName,
            "Call Recorder",
            NotificationManager.IMPORTANCE_LOW
        ).apply { lockscreenVisibility = Notification.VISIBILITY_SECRET }

        notificationManager.createNotificationChannel(channel)

        val notificationBuilder = NotificationCompat
            .Builder(this, channel.id)
            .setContentText("Standing by to record...")
            .setSmallIcon(R.drawable.ic_stat_name)
            .setShowWhen(false)

        pendingActivity?.let {
            val notificationIntent = Intent(this, it)
            val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
            notificationBuilder.setContentIntent(pendingIntent)
        }

        startForeground(NOTIFICATION_RECORDING_SERVICE_ID, notificationBuilder.build())
    }

    private fun observeCallStatusForRecording() {
        callStatusListener.callEventStatus
            .onEach { it.handleCallStatusChange() }
            .launchIn(lifecycleScope)
    }

    private suspend fun NewCallEvent.handleCallStatusChange() = when (callState) {
        CallState.STARTED -> callStarted()
        CallState.ENDED -> callEnded()
        CallState.RINGING -> Unit
    }

    private suspend fun NewCallEvent.callStarted() = with(callRecorder) {
        (recordingState.value as? RecordingState.Idle)?.startRecording(
            recordingJob = RecordingJob(prefs, this@callStarted),
            audioWriter = audioWriter
        )
    }

    private suspend fun callEnded() = with(callRecorder) {
        (recordingState.value as? RecordingState.IsRecording)?.stopRecording()
    }

    companion object {

        private const val ACTION_STOP = "ACTION_STOP"
        private const val EXTRA_NOTIFICATION_PENDING_ACTIVITY =
            "EXTRA_NOTIFICATION_PENDING_ACTIVITY"

        fun start(context: Context, notificationPendingActivity: KClass<out Activity>) {

            val intent = Intent(context, CallingService::class.java).apply {
                putExtra(
                    EXTRA_NOTIFICATION_PENDING_ACTIVITY,
                    notificationPendingActivity.java
                )
            }

            ContextCompat.startForegroundService(context, intent)
        }

        fun stop(context: Context) {

            val intent = Intent(context, CallingService::class.java)
            intent.action = ACTION_STOP

            ContextCompat.startForegroundService(context, intent)
        }
    }
}
