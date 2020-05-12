package com.redridgeapps.callrecorder.utils

import android.content.Context
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ToastMaker @Inject constructor(val context: Context) {

    fun showToast(text: String, duration: Int = Toast.LENGTH_LONG) {
        GlobalScope.launch(Dispatchers.Main) {
            Toast.makeText(context, text, duration).show()
        }
    }
}
