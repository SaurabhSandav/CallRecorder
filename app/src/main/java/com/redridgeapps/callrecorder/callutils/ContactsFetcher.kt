package com.redridgeapps.callrecorder.callutils

import android.content.ContentResolver
import android.net.Uri
import android.provider.ContactsContract
import androidx.core.database.getStringOrNull
import javax.inject.Inject

class ContactNameFetcher @Inject constructor(
    private val contentResolver: ContentResolver
) {

    fun getContactName(phoneNumber: String): String? {

        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(phoneNumber)
        )
        val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)

        contentResolver.query(uri, projection, null, null, null)?.use {

            if (it.moveToFirst())
                return it.getStringOrNull(0)
        }

        return null
    }
}
