package com.redridgeapps.callrecorder.callutils

import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import com.redridgeapps.callrecorder.callutils.CallDirection.INCOMING
import com.redridgeapps.callrecorder.callutils.CallDirection.OUTGOING
import com.redridgeapps.callrecorder.callutils.CallEventDetailed.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

class CallStatusListener : PhoneStateListener() {

    private var lastState = TelephonyManager.CALL_STATE_IDLE
    private var isIncoming: Boolean = false
    private val _callEventStatus = BroadcastChannel<NewCallEvent>(CONFLATED)

    val callEventStatus: Flow<NewCallEvent> = _callEventStatus.asFlow()

    override fun onCallStateChanged(state: Int, phoneNumber: String) {
        super.onCallStateChanged(state, phoneNumber)

        if (lastState == state) return

        lastState = state
        _callEventStatus.offer(NewCallEvent(inferCallStatus(state), phoneNumber))
    }

    private fun inferCallStatus(state: Int): CallEventDetailed {
        return when (state) {
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
}

enum class CallEventDetailed {
    MISSED_CALL,
    INCOMING_CALL_RECEIVED,
    INCOMING_CALL_ANSWERED,
    INCOMING_CALL_ENDED,
    OUTGOING_CALL_STARTED,
    OUTGOING_CALL_ENDED
}

enum class CallDirection {
    INCOMING,
    OUTGOING
}

enum class CallStatus {
    STARTED,
    ENDED,
    IDLE
}

data class NewCallEvent(
    val callEvent: CallEventDetailed,
    val phoneNumber: String
) {

    fun callDirection(): CallDirection = when (callEvent) {
        MISSED_CALL, INCOMING_CALL_RECEIVED, INCOMING_CALL_ANSWERED, INCOMING_CALL_ENDED -> INCOMING
        OUTGOING_CALL_STARTED, OUTGOING_CALL_ENDED -> OUTGOING
    }

    fun callStatus(): CallStatus = when (callEvent) {
        INCOMING_CALL_ANSWERED, OUTGOING_CALL_STARTED -> CallStatus.STARTED
        INCOMING_CALL_ENDED, OUTGOING_CALL_ENDED -> CallStatus.ENDED
        else -> CallStatus.IDLE
    }
}
