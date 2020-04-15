package com.redridgeapps.callrecorder.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun CoroutineScope.launchNoJob(block: suspend CoroutineScope.() -> Unit) {
    launch(block = block)
}
