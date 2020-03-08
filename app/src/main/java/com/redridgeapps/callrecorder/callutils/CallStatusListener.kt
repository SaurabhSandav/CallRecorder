package com.redridgeapps.callrecorder.callutils

import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import com.redridgeapps.callrecorder.callutils.CallStatus.*
import timber.log.Timber

class CallStatusListener constructor(
    private val onCallStateChanged: (CallStatus) -> Unit
) : PhoneStateListener() {

    private var lastState = TelephonyManager.CALL_STATE_IDLE
    private var isIncoming: Boolean = false

    override fun onCallStateChanged(state: Int, incomingNumber: String) {
        super.onCallStateChanged(state, incomingNumber)

        if (lastState == state) return

        val callStatus = inferCallStatus(state)

        onCallStateChanged(callStatus)
        logCallStatus(callStatus)

        lastState = state
    }

    private fun logCallStatus(status: CallStatus) {

        val message = when (status) {
            MissedCall -> "Missed call"
            IncomingCallReceived -> "Incoming call received"
            IncomingCallAnswered -> "Incoming call answered"
            IncomingCallEnded -> "Incoming call ended"
            OutgoingCallStarted -> "Outgoing call started"
            OutgoingCallEnded -> "Outgoing call ended"
        }

        Timber.d(message)
    }

    private fun inferCallStatus(state: Int): CallStatus {
        return when (state) {
            TelephonyManager.CALL_STATE_IDLE -> when {
                lastState == TelephonyManager.CALL_STATE_RINGING -> MissedCall
                isIncoming -> IncomingCallEnded
                else -> OutgoingCallEnded
            }
            TelephonyManager.CALL_STATE_OFFHOOK -> {
                isIncoming = (lastState == TelephonyManager.CALL_STATE_RINGING)
                when {
                    isIncoming -> IncomingCallAnswered
                    else -> OutgoingCallStarted
                }
            }
            TelephonyManager.CALL_STATE_RINGING -> {
                isIncoming = true
                IncomingCallReceived
            }
            else -> error("Unexpected error!")
        }
    }
}

enum class CallStatus {
    MissedCall,
    IncomingCallReceived,
    IncomingCallAnswered,
    IncomingCallEnded,
    OutgoingCallStarted,
    OutgoingCallEnded
}
