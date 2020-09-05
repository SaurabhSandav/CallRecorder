package com.redridgeapps.callutils.di.modules

import com.redridgeapps.callutils.services.CallingService
import com.redridgeapps.callutils.storage.RecordingStoragePathInitializer
import com.redridgeapps.callutils.workers.RecordingAutoDeleteWorker
import com.redridgeapps.common.StartupInitializer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ApplicationComponent::class)
abstract class InitializerModule {

    @Binds
    @IntoSet
    abstract fun provideCallingServiceInitializer(
        initializer: CallingService.Initializer,
    ): StartupInitializer

    @Binds
    @IntoSet
    abstract fun provideRecordingAutoDeleteWorkerInitializer(
        initializer: RecordingAutoDeleteWorker.Initializer,
    ): StartupInitializer

    @Binds
    @IntoSet
    abstract fun provideRecordingStoragePathInitializer(
        initializer: RecordingStoragePathInitializer,
    ): StartupInitializer
}
