package com.redridgeapps.callrecorder.callutils

import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import com.redridgeapps.callrecorder.callutils.CallStatus.*
import timber.log.Timber

private typealias OnCallStateChanged = (
    callStatus: CallStatus,
    phoneNumber: String,
    callType: String
) -> Unit

class CallStatusListener constructor(
    private val onCallStateChanged: OnCallStateChanged
) : PhoneStateListener() {

    private var lastState = TelephonyManager.CALL_STATE_IDLE
    private var isIncoming: Boolean = false

    override fun onCallStateChanged(state: Int, phoneNumber: String) {
        super.onCallStateChanged(state, phoneNumber)

        if (lastState == state) return

        val callStatus = inferCallStatus(state)

        onCallStateChanged(callStatus, phoneNumber, callStatus.callType())

        lastState = state
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
        }.also { Timber.d(it.name) }
    }

    private fun CallStatus.callType(): String = when (this) {
        MissedCall -> "Missed Call"
        IncomingCallReceived, IncomingCallAnswered, IncomingCallEnded -> "Incoming Call"
        OutgoingCallStarted, OutgoingCallEnded -> "Outgoing Call"
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
