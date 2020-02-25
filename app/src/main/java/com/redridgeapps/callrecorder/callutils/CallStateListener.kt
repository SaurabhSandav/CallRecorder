package com.redridgeapps.callrecorder.callutils

import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import com.redridgeapps.callrecorder.callutils.CallStateListener.CallStatus.*
import timber.log.Timber
import java.util.*

class CallStateListener(
    private val callRecorder: CallRecorder
) : PhoneStateListener() {

    private var lastState = TelephonyManager.CALL_STATE_IDLE
    private var isIncoming: Boolean = false
    private var callStartTime: Date = Date()

    override fun onCallStateChanged(state: Int, incomingNumber: String) {
        super.onCallStateChanged(state, incomingNumber)

        if (lastState == state) return

        logCallStatus(state)

        when (detectCallStatus(state)) {
            MissedCall, IncomingCallReceived -> {
            }
            IncomingCallAnswered, OutgoingCallStarted -> callRecorder.startRecording()
            IncomingCallEnded, OutgoingCallEnded -> callRecorder.stopRecording()
        }

        lastState = state
    }

    private fun logCallStatus(state: Int) {
        val message = when (detectCallStatus(state)) {
            MissedCall -> "Missed call"
            IncomingCallReceived -> "Incoming call received"
            IncomingCallAnswered -> "Incoming call answered"
            IncomingCallEnded -> "Incoming call ended"
            OutgoingCallStarted -> "Outgoing call started"
            OutgoingCallEnded -> "Outgoing call ended"
        }

        Timber.d(message)
    }

    private fun detectCallStatus(state: Int): CallStatus = when (state) {
        TelephonyManager.CALL_STATE_IDLE -> when {
            lastState == TelephonyManager.CALL_STATE_RINGING -> MissedCall
            isIncoming -> IncomingCallEnded
            else -> OutgoingCallEnded
        }
        TelephonyManager.CALL_STATE_OFFHOOK -> {
            if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                isIncoming = false
                callStartTime = Date()

                OutgoingCallStarted
            } else {
                isIncoming = true
                callStartTime = Date()

                IncomingCallAnswered
            }
        }
        TelephonyManager.CALL_STATE_RINGING -> {
            isIncoming = true
            callStartTime = Date()

            IncomingCallReceived
        }
        else -> error("Unexpected error!")
    }

    enum class CallStatus {
        MissedCall,
        IncomingCallReceived,
        IncomingCallAnswered,
        IncomingCallEnded,
        OutgoingCallStarted,
        OutgoingCallEnded
    }
}
