package com.redridgeapps.callutils.callevents

import com.redridgeapps.callutils.callevents.CallEvent.INCOMING_CALL_ANSWERED
import com.redridgeapps.callutils.callevents.CallEvent.INCOMING_CALL_ENDED
import com.redridgeapps.callutils.callevents.CallEvent.INCOMING_CALL_RECEIVED
import com.redridgeapps.callutils.callevents.CallEvent.MISSED_CALL
import com.redridgeapps.callutils.callevents.CallEvent.OUTGOING_CALL_ENDED
import com.redridgeapps.callutils.callevents.CallEvent.OUTGOING_CALL_STARTED

data class NewCallEvent(
    val event: CallEvent,
    val phoneNumber: String,
) {

    val callDirection: CallDirection = when (event) {
        MISSED_CALL, INCOMING_CALL_RECEIVED, INCOMING_CALL_ANSWERED, INCOMING_CALL_ENDED,
        -> CallDirection.INCOMING
        OUTGOING_CALL_STARTED, OUTGOING_CALL_ENDED -> CallDirection.OUTGOING
    }

    val callState: CallState = when (event) {
        INCOMING_CALL_ANSWERED, OUTGOING_CALL_STARTED -> CallState.STARTED
        INCOMING_CALL_ENDED, OUTGOING_CALL_ENDED, MISSED_CALL -> CallState.ENDED
        INCOMING_CALL_RECEIVED -> CallState.RINGING
    }
}
