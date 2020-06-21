package com.redridgeapps.callrecorder.utils.timber

import android.content.Context
import com.redridgeapps.callrecorder.BuildConfig
import com.redridgeapps.callrecorder.common.StartupInitializer
import timber.log.Timber
import javax.inject.Inject

class TimberInitializer @Inject constructor() : StartupInitializer {

    override fun initialize(context: Context) {
        when {
            BuildConfig.DEBUG -> Timber.plant(HyperlinkedDebugTree)
            else -> TODO()
        }
    }
}
