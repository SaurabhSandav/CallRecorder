package com.redridgeapps.callrecorder.di.modules

import com.redridgeapps.callrecorder.callutils.CallPlayback
import com.redridgeapps.callrecorder.callutils.CallRecorder
import com.redridgeapps.callrecorder.utils.Systemizer
import com.redridgeapps.repository.ICallPlayback
import com.redridgeapps.repository.ICallRecorder
import com.redridgeapps.repository.ISystemizer
import dagger.Binds
import dagger.Module

@Module
abstract class RepositoryModule {

    @Binds
    abstract fun provideICallRecorder(callRecorder: CallRecorder): ICallRecorder

    @Binds
    abstract fun provideICallPlayback(callPlayback: CallPlayback): ICallPlayback

    @Binds
    abstract fun provideISystemizer(systemizer: Systemizer): ISystemizer
}
