package com.redridgeapps.callrecorder.services

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.redridgeapps.callrecorder.di.modules.android.PerService
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope

@Module
object CallingServiceModule {

    @Provides
    @PerService
    fun provideLifecycle(service: CallingService): Lifecycle {
        return service.lifecycle
    }

    @Provides
    @PerService
    fun provideCoroutineScope(service: CallingService): CoroutineScope {
        return service.lifecycleScope
    }
}
