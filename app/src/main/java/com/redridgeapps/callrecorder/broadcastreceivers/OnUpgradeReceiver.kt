package com.redridgeapps.callrecorder.broadcastreceivers

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.redridgeapps.callrecorder.services.CallingService

class OnUpgradeReceiver : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {

        CallingService.startSurveillance(context)
    }
}
