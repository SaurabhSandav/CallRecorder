package com.redridgeapps.callrecorder

import android.app.Application
import com.redridgeapps.callrecorder.di.AppComponent
import com.redridgeapps.callrecorder.di.DaggerAppComponent
import com.redridgeapps.callrecorder.services.CallingService
import com.redridgeapps.callrecorder.utils.HyperlinkedDebugTree
import com.redridgeapps.callrecorder.utils.prefs.PREF_IS_RECORDING_ON
import com.redridgeapps.callrecorder.utils.prefs.Prefs
import com.topjohnwu.superuser.Shell
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class App : Application(), HasAndroidInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var prefs: Prefs

    private lateinit var appComponent: AppComponent

    init {
        Shell.Config.setFlags(Shell.FLAG_REDIRECT_STDERR)
        Shell.Config.verboseLogging(false)
        Shell.Config.setTimeout(10)
    }

    override fun androidInjector(): AndroidInjector<Any> = dispatchingAndroidInjector

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.factory().create(this)
        appComponent.inject(this)

        setupTimber()
        setupCallingService()
    }

    private fun setupTimber() {
        if (BuildConfig.DEBUG)
            Timber.plant(HyperlinkedDebugTree)
        else TODO()
    }

    private fun setupCallingService() {

        prefs.getFlow(PREF_IS_RECORDING_ON)
            .onEach { recordingOn ->
                if (recordingOn)
                    CallingService.start(this@App)
                else
                    CallingService.stop(this@App)
            }
            .launchIn(GlobalScope)
    }
}
