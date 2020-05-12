package com.redridgeapps.callrecorder.utils

import android.content.Context
import android.widget.Toast
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ToastMaker @Inject constructor(val context: Context) {

    fun showToast(text: String, duration: Int = Toast.LENGTH_LONG) {
        Toast.makeText(context, text, duration).show()
    }
}
