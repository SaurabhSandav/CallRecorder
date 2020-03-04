package com.redridgeapps.callrecorder.di.modules

import com.redridgeapps.callrecorder.callutils.CallPlayback
import com.redridgeapps.repository.ICallPlayback
import dagger.Binds
import dagger.Module

@Module
abstract class RepositoryModule {

    @Binds
    abstract fun provideICallPlayback(callPlayback: CallPlayback): ICallPlayback
}
