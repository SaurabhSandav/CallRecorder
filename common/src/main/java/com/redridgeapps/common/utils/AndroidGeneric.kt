package com.redridgeapps.common.utils

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity

fun Context.findComponentActivity(): ComponentActivity? {
    return when (this) {
        is ComponentActivity -> this
        is ContextWrapper -> this.baseContext.findComponentActivity()
        else -> null
    }
}

fun Context.getComponentActivity(): ComponentActivity {
    return findComponentActivity() ?: error("ComponentActivity not found in ContextAmbient")
}
