package com.redridgeapps.callrecorder.di

import android.app.Activity
import com.redridgeapps.callrecorder.App
import com.redridgeapps.callrecorder.di.modules.AppModule
import com.redridgeapps.callrecorder.di.modules.android.ActivityInjectionModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AndroidInjectionModule::class,
        ActivityInjectionModule::class,
        AppModule::class
    ]
)
interface AppComponent : AndroidInjector<App> {

    fun activityProviders(): Map<Class<out Activity>, Provider<Activity>>

    @Component.Factory
    interface Builder : AndroidInjector.Factory<App> {

        override fun create(@BindsInstance instance: App): AppComponent
    }
}
