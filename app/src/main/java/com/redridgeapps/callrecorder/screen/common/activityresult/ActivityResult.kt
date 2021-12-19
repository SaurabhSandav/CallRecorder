package com.redridgeapps.callrecorder.screen.common.activityresult

import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.Composable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.redridgeapps.common.utils.getComponentActivity

@Composable
internal fun <I, O> rememberActivityResultManager(
    key: String,
    contract: ActivityResultContract<I, O>,
    callback: ActivityResultCallback<O>,
): ActivityResultManager<I, O> {

    val context = LocalContext.current

    return remember {
        val activityResultRegistry = context.getComponentActivity().activityResultRegistry
        ActivityResultManager(key, activityResultRegistry, contract, callback)
    }
}

internal class ActivityResultManager<I, O>(
    private val key: String,
    private val activityResultRegistry: ActivityResultRegistry,
    private val contract: ActivityResultContract<I, O>,
    private val callback: ActivityResultCallback<O>,
) : RememberObserver {

    private var resultLauncher: ActivityResultLauncher<I>? = null

    fun launch(input: I) {
        resultLauncher?.launch(input)
    }

    override fun onRemembered() {
        resultLauncher = activityResultRegistry.register(key, contract, callback)
    }

    override fun onAbandoned() {
        resultLauncher?.unregister()
        resultLauncher = null
    }

    override fun onForgotten() {
        resultLauncher?.unregister()
        resultLauncher = null
    }
}
