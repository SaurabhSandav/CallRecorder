package com.redridgeapps.callrecorder.broadcastreceivers

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 *
 * Placeholder BroadcastReceiver to start App on boot.
 * The relevant code (starting Calling Service) to be run on boot resides in App class.
 *
 */
class OnBootReceiver : BroadcastReceiver() {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context, intent: Intent) {
    }
}
