package com.redridgeapps.callrecorder.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.lifecycle.LifecycleService
import com.redridgeapps.callrecorder.MainActivity
import com.redridgeapps.callrecorder.callutils.CallRecorder
import com.redridgeapps.callrecorder.callutils.CallStateListener
import com.redridgeapps.callrecorder.callutils.RecordingAPI

class CallingService : LifecycleService() {

    private lateinit var telephonyManager: TelephonyManager
    private lateinit var callStateListener: CallStateListener

    override fun onCreate() {
        super.onCreate()

        telephonyManager = getSystemService()!!

        val callRecorder = CallRecorder(RecordingAPI.AudioRecord, application, lifecycle)
        callStateListener = CallStateListener(callRecorder)

        createNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        telephonyManager.listen(
            callStateListener,
            PhoneStateListener.LISTEN_CALL_STATE
        )

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    override fun onDestroy() {
        super.onDestroy()

        telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_NONE)
    }

    private fun createNotification() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )

        val notification: Notification =
            NotificationCompat.Builder(this, createNotificationChannel())
                .setContentTitle("Example Service")
                .setContentText("Standing by to record...")
//                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .build()

        startForeground(1, notification)
    }

    private fun createNotificationChannel(): String {
        val chan =
            NotificationChannel("c4c54c44c3", "Call Recorder", NotificationManager.IMPORTANCE_NONE)
        chan.lockscreenVisibility = Notification.VISIBILITY_SECRET
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return chan.id
    }

    companion object {

        fun startSurveillance(context: Context) {

            val callingServiceIntent = Intent(context, CallingService::class.java)
            ContextCompat.startForegroundService(context, callingServiceIntent)
        }
    }
}
