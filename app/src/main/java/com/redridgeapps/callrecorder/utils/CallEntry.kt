package com.redridgeapps.callrecorder.utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.provider.CallLog
import javax.inject.Inject

data class CallEntry(
    val number: String,
    val name: String,
    val date: Long,
    val type: String,
    val duration: Long
)

class CallLogFetcher @Inject constructor(
    private val contentResolver: ContentResolver
) {

    @SuppressLint("MissingPermission")
    fun getLastCallEntry(): CallEntry? {

        val strOrder = CallLog.Calls.DATE + " DESC"
        val callUri = CallLog.Calls.CONTENT_URI
        val cur = contentResolver.query(callUri, null, null, null, strOrder)

        cur ?: error("Cursor query failed!")

        fun String.columnIndex() = cur.getColumnIndex(this)

        cur.use {
            if (!cur.moveToNext()) return null

            val callNumber = cur.getString(CallLog.Calls.NUMBER.columnIndex())
            val callName = cur.getString(CallLog.Calls.CACHED_NAME.columnIndex())
            val callDate = cur.getLong(CallLog.Calls.DATE.columnIndex())
            val callType = cur.getInt(CallLog.Calls.TYPE.columnIndex()).asCallTypeStr()
            val duration = cur.getLong(CallLog.Calls.DURATION.columnIndex())

            return CallEntry(callNumber, callName, callDate, callType, duration)
        }
    }

    private fun Int.asCallTypeStr(): String = when (this) {
        CallLog.Calls.INCOMING_TYPE -> "INCOMING"
        CallLog.Calls.OUTGOING_TYPE -> "OUTGOING"
        CallLog.Calls.MISSED_TYPE -> "MISSED"
        CallLog.Calls.VOICEMAIL_TYPE -> "VOICEMAIL"
        CallLog.Calls.REJECTED_TYPE -> "REJECTED"
        CallLog.Calls.BLOCKED_TYPE -> "BLOCKED"
        else -> error("Unsupported call type")
    }
}
