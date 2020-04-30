package com.redridgeapps.callrecorder.callutils

import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import com.redridgeapps.callrecorder.callutils.CallDirection.INCOMING
import com.redridgeapps.callrecorder.callutils.CallDirection.OUTGOING
import com.redridgeapps.callrecorder.callutils.CallStatus.IncomingCallAnswered
import com.redridgeapps.callrecorder.callutils.CallStatus.IncomingCallEnded
import com.redridgeapps.callrecorder.callutils.CallStatus.IncomingCallReceived
import com.redridgeapps.callrecorder.callutils.CallStatus.MissedCall
import com.redridgeapps.callrecorder.callutils.CallStatus.OutgoingCallEnded
import com.redridgeapps.callrecorder.callutils.CallStatus.OutgoingCallStarted
import timber.log.Timber

private typealias OnCallStateChanged = (
    callStatus: CallStatus,
    phoneNumber: String,
    callDirection: CallDirection
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
            else -> error("Unexpected call state!")
        }.also { Timber.d(it.name) }
    }

    private fun CallStatus.callType(): CallDirection = when (this) {
        MissedCall, IncomingCallReceived, IncomingCallAnswered, IncomingCallEnded -> INCOMING
        OutgoingCallStarted, OutgoingCallEnded -> OUTGOING
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
