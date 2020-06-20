package com.redridgeapps.callrecorder

import android.app.Application
import com.redridgeapps.callrecorder.callutils.Defaults
import com.redridgeapps.callrecorder.callutils.services.CallingService
import com.redridgeapps.callrecorder.prefs.PREF_RECORDING_ENABLED
import com.redridgeapps.callrecorder.prefs.Prefs
import com.redridgeapps.callrecorder.utils.HyperlinkedDebugTree
import com.topjohnwu.superuser.Shell
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    @Inject
    lateinit var prefs: Prefs

    init {
        Shell.Config.setFlags(Shell.FLAG_REDIRECT_STDERR)
        Shell.Config.verboseLogging(false)
        Shell.Config.setTimeout(10)
    }

    override fun onCreate() {
        super.onCreate()

        setupTimber()
        setupCallingService()
    }

    private fun setupTimber() {
        if (BuildConfig.DEBUG)
            Timber.plant(HyperlinkedDebugTree)
        else TODO()
    }

    private fun setupCallingService() {

        prefs.prefBoolean(PREF_RECORDING_ENABLED) { Defaults.RECORDING_ENABLED }
            .onEach { recordingOn ->
                when {
                    recordingOn -> CallingService.start(this@App, MainActivity::class)
                    else -> CallingService.stop(this@App)
                }
            }
            .launchIn(GlobalScope)
    }
}
