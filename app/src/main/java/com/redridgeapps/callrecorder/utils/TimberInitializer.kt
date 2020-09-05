package com.redridgeapps.callrecorder.utils

import android.content.Context
import com.redridgeapps.callrecorder.BuildConfig
import com.redridgeapps.common.StartupInitializer
import timber.log.Timber
import javax.inject.Inject

class TimberInitializer @Inject constructor() : StartupInitializer {

    override fun initialize(context: Context) {

        if (BuildConfig.DEBUG) {
            Timber.plant(HyperlinkedDebugTree)
        }
    }
}

private object HyperlinkedDebugTree : Timber.DebugTree() {

    override fun createStackElementTag(element: StackTraceElement): String? {
        return "(${element.fileName}:${element.lineNumber})"
    }
}
