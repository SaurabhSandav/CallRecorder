package com.redridgeapps.callrecorder.di

import com.redridgeapps.callrecorder.App
import com.redridgeapps.callrecorder.di.modules.AppModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class
    ]
)
interface AppComponent : AndroidInjector<App> {

    @Component.Factory
    interface Factory : AndroidInjector.Factory<App> {

        override fun create(@BindsInstance instance: App): AppComponent
    }
}
