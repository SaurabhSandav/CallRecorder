package com.redridgeapps.callrecorder

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.redridgeapps.common.StartupInitializer
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var startupInitializers: Set<@JvmSuppressWildcards StartupInitializer>

    override fun onCreate() {
        super.onCreate()

        setupStartupInitialization()
    }

    private fun setupStartupInitialization() {
        startupInitializers.forEach { it.initialize(this@App) }
        startupInitializers = emptySet()
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }
}
