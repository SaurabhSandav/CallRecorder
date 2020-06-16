package com.redridgeapps.callrecorder.callutils.callevents

import com.redridgeapps.callrecorder.callutils.callevents.CallEvent.*

data class NewCallEvent(
    val event: CallEvent,
    val phoneNumber: String
) {

    val callDirection: CallDirection = when (event) {
        MISSED_CALL, INCOMING_CALL_RECEIVED, INCOMING_CALL_ANSWERED, INCOMING_CALL_ENDED
        -> CallDirection.INCOMING
        OUTGOING_CALL_STARTED, OUTGOING_CALL_ENDED -> CallDirection.OUTGOING
    }

    val callState: CallState = when (event) {
        INCOMING_CALL_ANSWERED, OUTGOING_CALL_STARTED -> CallState.STARTED
        INCOMING_CALL_ENDED, OUTGOING_CALL_ENDED, MISSED_CALL -> CallState.ENDED
        INCOMING_CALL_RECEIVED -> CallState.RINGING
    }
}
