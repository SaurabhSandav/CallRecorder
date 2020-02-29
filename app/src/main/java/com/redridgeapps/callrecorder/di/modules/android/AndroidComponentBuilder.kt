package com.redridgeapps.callrecorder.di.modules.android

import com.redridgeapps.callrecorder.MainActivity
import com.redridgeapps.callrecorder.MainActivityModule
import com.redridgeapps.callrecorder.di.modules.RepositoryModule
import dagger.Module
import dagger.android.ContributesAndroidInjector
import javax.inject.Scope

@Module
abstract class AndroidComponentBuilder {

    // Activities

    @ContributesAndroidInjector(
        modules = [
            ActivityModule::class,
            MainActivityModule::class,
            RepositoryModule::class
        ]
    )
    @PerActivity
    abstract fun bindMainActivity(): MainActivity
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class PerActivity
