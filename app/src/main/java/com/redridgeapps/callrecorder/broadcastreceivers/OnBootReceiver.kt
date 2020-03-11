package com.redridgeapps.callrecorder.broadcastreceivers

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.redridgeapps.callrecorder.services.CallingService
import com.redridgeapps.callrecorder.utils.prefs.Prefs
import dagger.android.AndroidInjection
import javax.inject.Inject

class OnBootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var prefs: Prefs

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
        AndroidInjection.inject(this, context)

        CallingService.startIfRecordingOn(context, prefs)
    }
}
