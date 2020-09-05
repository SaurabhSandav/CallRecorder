package com.redridgeapps.callutils.callevents

enum class CallEvent {
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

enum class CallState {
    STARTED,
    ENDED,
    RINGING
}
