package com.redridgeapps.callrecorder

import android.app.Application
import com.redridgeapps.callrecorder.services.CallingService
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        setupTimber()

        CallingService.startSurveillance(this)
    }

    private fun setupTimber() {
        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())
        else TODO()
    }
}
