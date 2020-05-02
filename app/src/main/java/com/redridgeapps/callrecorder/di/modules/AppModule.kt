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
interface AppModule {

    @Binds
    fun App.bindContext(): Context

    companion object {

        @Provides
        fun Context.provideSharedPreferences(): SharedPreferences {
            return PreferenceManager.getDefaultSharedPreferences(this)
        }

        @Provides
        fun Context.provideAudioManager(): AudioManager = getSystemService()!!

        @Provides
        fun Context.provideTelephonyManager(): TelephonyManager = getSystemService()!!

        @Provides
        fun Context.provideNotificationManager(): NotificationManager = getSystemService()!!

        @Provides
        fun Context.providePowerManager(): PowerManager = getSystemService()!!

        @Provides
        fun Context.provideContentResolver(): ContentResolver = contentResolver
    }
}
