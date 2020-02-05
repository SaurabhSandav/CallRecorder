package com.redridgeapps.callrecorder

import androidx.compose.Composable
import androidx.compose.onCommit
import androidx.compose.remember
import androidx.compose.state
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.afollestad.assent.Permission

@Composable
fun <T : Any> observe(data: LiveData<T>): T {

    var result by state {
        data.value ?: error("LiveData used in Compose requires an Initial value")
    }
    val observer = remember { Observer<T> { result = it } }

    onCommit(data) {
        data.observeForever(observer)
        onDispose { data.removeObserver(observer) }
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
