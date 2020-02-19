package com.redridgeapps.callrecorder.ui

import androidx.compose.Composable
import androidx.compose.onCommit
import androidx.compose.remember
import androidx.compose.state
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.afollestad.assent.Permission

@Composable
fun <T : Any> LiveData<T>.latestValue(): T? {

    var result by state { value }
    val observer = remember { Observer<T> { result = it } }

    onCommit(this) {
        observeForever(observer)
        onDispose { removeObserver(observer) }
    }

    return result
}

@Composable
fun WithPermission(permissions: Array<Permission>, block: () -> Unit) {
/*
    ambient(key = ActivityAmbient) as Activity
        .runWithPermissions(*permissions) { block() }
*/
}
