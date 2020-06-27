package com.redridgeapps.callrecorder.callutils.di.modules

import com.redridgeapps.callrecorder.callutils.services.CallingService
import com.redridgeapps.callrecorder.callutils.storage.RecordingStoragePathInitializer
import com.redridgeapps.callrecorder.callutils.workers.RecordingAutoDeleteWorker
import com.redridgeapps.callrecorder.common.StartupInitializer
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
        initializer: CallingService.Initializer
    ): StartupInitializer

    @Binds
    @IntoSet
    abstract fun provideRecordingAutoDeleteWorkerInitializer(
        initializer: RecordingAutoDeleteWorker.Initializer
    ): StartupInitializer

    @Binds
    @IntoSet
    abstract fun provideRecordingStoragePathInitializer(
        initializer: RecordingStoragePathInitializer
    ): StartupInitializer
}
