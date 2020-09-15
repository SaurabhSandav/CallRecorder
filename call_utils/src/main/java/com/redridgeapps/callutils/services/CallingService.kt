package com.redridgeapps.callutils.services

import android.app.Activity
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
import androidx.datastore.DataStore
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.redridgeapps.callutils.R
import com.redridgeapps.callutils.callevents.CallState
import com.redridgeapps.callutils.callevents.CallStatusListener
import com.redridgeapps.callutils.callevents.NewCallEvent
import com.redridgeapps.callutils.recording.AudioWriter
import com.redridgeapps.callutils.recording.CallRecorder
import com.redridgeapps.callutils.recording.RecordingJob
import com.redridgeapps.callutils.recording.RecordingState
import com.redridgeapps.common.AppDispatchers
import com.redridgeapps.common.StartupInitializer
import com.redridgeapps.common.constants.NOTIFICATION_RECORDING_SERVICE_ID
import com.redridgeapps.common.di.qualifiers.NotificationPendingActivity
import com.redridgeapps.prefs.Prefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.KClass

@AndroidEntryPoint
class CallingService : LifecycleService() {

    @Inject
    lateinit var prefs: DataStore<Prefs>

    @Inject
    lateinit var telephonyManager: TelephonyManager

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var callRecorder: CallRecorder

    @Inject
    lateinit var dispatchers: AppDispatchers

    private val callStatusListener = CallStatusListener()
    private val audioWriter by lazy { AudioWriter(lifecycleScope, dispatchers) }
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

        @Suppress("UNCHECKED_CAST")
        pendingActivity = intent
            ?.getSerializableExtra(EXTRA_NOTIFICATION_PENDING_ACTIVITY) as Class<out Activity>?
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

        internal fun start(context: Context, notificationPendingActivity: KClass<out Activity>) {

            val intent = Intent(context, CallingService::class.java).apply {
                putExtra(
                    EXTRA_NOTIFICATION_PENDING_ACTIVITY,
                    notificationPendingActivity.java
                )
            }

            ContextCompat.startForegroundService(context, intent)
        }

        internal fun stop(context: Context) {

            val intent = Intent(context, CallingService::class.java)
            intent.action = ACTION_STOP

            ContextCompat.startForegroundService(context, intent)
        }
    }

    class Initializer @Inject constructor(
        private val prefs: DataStore<Prefs>,
        @NotificationPendingActivity private val notificationPendingActivity: KClass<out Activity>,
    ) : StartupInitializer {

        override fun initialize(context: Context) {

            prefs.data.distinctUntilChangedBy { it.is_recording_enabled }.onEach {
                when {
                    it.is_recording_enabled -> start(context, notificationPendingActivity)
                    else -> stop(context)
                }
            }.launchIn(GlobalScope)
        }
    }
}
