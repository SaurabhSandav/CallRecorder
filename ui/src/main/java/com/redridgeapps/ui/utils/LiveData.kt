package com.redridgeapps.ui.utils

import androidx.compose.Composable
import androidx.compose.onCommit
import androidx.compose.remember
import androidx.compose.state
import com.redridgeapps.repository.ILiveData

@Composable
fun <T : Any> ILiveData<T>.latestValue(): T? {

    var result by state { value }
    val observer = remember { { newValue: T -> result = newValue } }

    onCommit(this) {
        observeForever(observer)
        onDispose { removeObserver(observer) }
    }

    return result
}
