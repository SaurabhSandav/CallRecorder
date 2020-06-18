package com.redridgeapps.callrecorder.di.modules

import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import android.telephony.TelephonyManager
import androidx.core.content.getSystemService
import androidx.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    fun @receiver:ApplicationContext Context.provideSharedPreferences(): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(this)
    }

    @Provides
    fun @receiver:ApplicationContext Context.provideTelephonyManager(): TelephonyManager =
        getSystemService()!!

    @Provides
    fun @receiver:ApplicationContext Context.provideNotificationManager(): NotificationManager =
        getSystemService()!!

    @Provides
    fun @receiver:ApplicationContext Context.provideContentResolver(): ContentResolver =
        contentResolver
}
