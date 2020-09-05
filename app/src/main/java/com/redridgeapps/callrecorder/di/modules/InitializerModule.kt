package com.redridgeapps.callrecorder.di.modules

import com.redridgeapps.callrecorder.utils.TimberInitializer
import com.redridgeapps.common.StartupInitializer
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
