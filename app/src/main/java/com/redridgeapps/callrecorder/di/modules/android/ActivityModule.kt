package com.redridgeapps.callrecorder.di.modules.android

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import dagger.Module
import dagger.Provides

@Module
object ActivityModule {

    @Provides
    @PerActivity
    fun provideActivityLifecycle(activity: AppCompatActivity): Lifecycle {
        return activity.lifecycle
    }
}
