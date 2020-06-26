package com.redridgeapps.callrecorder.di.modules

import com.redridgeapps.callrecorder.common.StartupInitializer
import com.redridgeapps.callrecorder.utils.timber.TimberInitializer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(ApplicationComponent::class)
abstract class InitializerModule {

    @IntoSet
    @Binds
    abstract fun TimberInitializer.provideTimberInitializer(): StartupInitializer
}
