package com.redridgeapps.callrecorder

import android.app.Application
import com.redridgeapps.callrecorder.services.CallingService
import com.topjohnwu.superuser.Shell
import timber.log.Timber

class App : Application() {

    init {
        Shell.Config.setFlags(Shell.FLAG_REDIRECT_STDERR)
        Shell.Config.verboseLogging(BuildConfig.DEBUG)
        Shell.Config.setTimeout(10)
    }

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
