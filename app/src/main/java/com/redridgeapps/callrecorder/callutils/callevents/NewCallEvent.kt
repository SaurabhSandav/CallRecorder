package com.redridgeapps.callrecorder.callutils.callevents

data class NewCallEvent(
    val event: CallEvent,
    val phoneNumber: String
) {

    val callDirection: CallDirection = when (event) {
        CallEvent.MISSED_CALL, CallEvent.INCOMING_CALL_RECEIVED,
        CallEvent.INCOMING_CALL_ANSWERED, CallEvent.INCOMING_CALL_ENDED -> CallDirection.INCOMING
        CallEvent.OUTGOING_CALL_STARTED, CallEvent.OUTGOING_CALL_ENDED -> CallDirection.OUTGOING
    }

    val callState: CallState = when (event) {
        CallEvent.INCOMING_CALL_ANSWERED, CallEvent.OUTGOING_CALL_STARTED -> CallState.STARTED
        CallEvent.INCOMING_CALL_ENDED, CallEvent.OUTGOING_CALL_ENDED, CallEvent.MISSED_CALL -> CallState.ENDED
        CallEvent.INCOMING_CALL_RECEIVED -> CallState.RINGING
    }
}
