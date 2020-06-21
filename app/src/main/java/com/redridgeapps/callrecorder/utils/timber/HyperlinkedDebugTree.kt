package com.redridgeapps.callrecorder.utils.timber

import timber.log.Timber

internal object HyperlinkedDebugTree : Timber.DebugTree() {

    override fun createStackElementTag(element: StackTraceElement): String? {
        return "(${element.fileName}:${element.lineNumber})"
    }
}
