package com.redridgeapps.callrecorder.viewmodel.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun CoroutineScope.launchNoJob(block: suspend CoroutineScope.() -> Unit) {
    launch(block = block)
}
