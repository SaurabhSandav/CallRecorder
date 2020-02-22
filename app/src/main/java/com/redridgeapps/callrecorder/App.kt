package com.redridgeapps.callrecorder

import android.app.Application
import com.redridgeapps.callrecorder.services.CallingService

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        CallingService.startSurveillance(this)
    }
}
