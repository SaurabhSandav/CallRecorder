package com.redridgeapps.callrecorder.di.modules

import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import android.media.AudioManager
import android.os.PowerManager
import android.telephony.TelephonyManager
import androidx.core.content.getSystemService
import androidx.preference.PreferenceManager
import com.redridgeapps.callrecorder.App
import com.redridgeapps.callrecorder.di.modules.android.AndroidComponentBuilder
import com.redridgeapps.callrecorder.di.modules.android.ViewModelModule
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjectionModule

@Module(
    includes = [
        AndroidInjectionModule::class,
        AndroidComponentBuilder::class,
        ViewModelModule::class,
        DBModule::class
    ]
)
abstract class AppModule {

    @Binds
    abstract fun provideContext(app: App): Context

    companion object {

        @Provides
        fun provideSharedPreferences(context: Context): SharedPreferences {
            return PreferenceManager.getDefaultSharedPreferences(context)
        }

        @Provides
        fun provideAudioManager(context: Context): AudioManager {
            return context.getSystemService()!!
        }

        @Provides
        fun provideTelephonyManager(context: Context): TelephonyManager {
            return context.getSystemService()!!
        }

        @Provides
        fun provideNotificationManager(context: Context): NotificationManager {
            return context.getSystemService()!!
        }

        @Provides
        fun providePowerManager(context: Context): PowerManager {
            return context.getSystemService()!!
        }

        @Provides
        fun provideContentResolver(context: Context): ContentResolver {
            return context.contentResolver
        }
    }
}
