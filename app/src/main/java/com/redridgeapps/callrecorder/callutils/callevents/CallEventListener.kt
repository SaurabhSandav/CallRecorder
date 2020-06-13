package com.redridgeapps.callrecorder.callutils.callevents

import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import com.redridgeapps.callrecorder.callutils.callevents.CallEvent.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class CallStatusListener : PhoneStateListener() {

    private var lastState = TelephonyManager.CALL_STATE_IDLE
    private var isIncoming: Boolean = false
    private val _callEventStatus = MutableStateFlow(NewCallEvent(MISSED_CALL, ""))

    val callEventStatus: Flow<NewCallEvent> = _callEventStatus

    override fun onCallStateChanged(state: Int, phoneNumber: String) {
        super.onCallStateChanged(state, phoneNumber)

        if (lastState == state) return

        lastState = state
        _callEventStatus.value = NewCallEvent(extrapolateCallEvent(state), phoneNumber)
    }

    private fun extrapolateCallEvent(state: Int): CallEvent = when (state) {
        TelephonyManager.CALL_STATE_IDLE -> when {
            lastState == TelephonyManager.CALL_STATE_RINGING -> MISSED_CALL
            isIncoming -> INCOMING_CALL_ENDED
            else -> OUTGOING_CALL_ENDED
        }
        TelephonyManager.CALL_STATE_OFFHOOK -> {
            isIncoming = lastState == TelephonyManager.CALL_STATE_RINGING
            when {
                isIncoming -> INCOMING_CALL_ANSWERED
                else -> OUTGOING_CALL_STARTED
            }
        }
        TelephonyManager.CALL_STATE_RINGING -> {
            isIncoming = true
            INCOMING_CALL_RECEIVED
        }
        else -> error("Unexpected call state!")
    }
}
