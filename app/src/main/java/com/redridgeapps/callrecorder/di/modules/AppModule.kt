package com.redridgeapps.callrecorder.di.modules

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.redridgeapps.callrecorder.App
import com.redridgeapps.callrecorder.di.modules.android.AndroidComponentBuilder
import com.redridgeapps.callrecorder.di.modules.android.UIInitializerModule
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Module(
    includes = [
        AndroidInjectionModule::class,
        AndroidComponentBuilder::class,
        UIInitializerModule::class
    ]
)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun provideContext(app: App): Context

    companion object {

        @Provides
        @Singleton
        fun provideSharedPreferences(context: Context): SharedPreferences {
            return PreferenceManager.getDefaultSharedPreferences(context)
        }
    }
}
