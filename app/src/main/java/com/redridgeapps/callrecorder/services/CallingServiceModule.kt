package com.redridgeapps.callrecorder.services

import androidx.lifecycle.Lifecycle
import com.redridgeapps.callrecorder.di.modules.android.PerService
import dagger.Module
import dagger.Provides

@Module
object CallingServiceModule {

    @Provides
    @PerService
    fun provideLifecycle(service: CallingService): Lifecycle {
        return service.lifecycle
    }
}
