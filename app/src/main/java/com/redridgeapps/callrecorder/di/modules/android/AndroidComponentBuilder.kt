package com.redridgeapps.callrecorder.di.modules.android

import com.redridgeapps.callrecorder.MainActivity
import com.redridgeapps.callrecorder.MainActivityModule
import com.redridgeapps.callrecorder.services.CallingService
import com.redridgeapps.callrecorder.services.CallingServiceModule
import dagger.Module
import dagger.android.ContributesAndroidInjector
import javax.inject.Scope

@Module
abstract class AndroidComponentBuilder {

    // region Activities

    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    @PerActivity
    abstract fun bindMainActivity(): MainActivity

    // endregion Activities

    // region Services

    @ContributesAndroidInjector(modules = [CallingServiceModule::class])
    @PerService
    abstract fun bindCallingService(): CallingService

    // endregion Services
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class PerActivity

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class PerService
