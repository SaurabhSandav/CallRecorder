package com.redridgeapps.callrecorder.di.modules

import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.redridgeapps.callrecorder.App
import com.redridgeapps.callrecorder.RecordingQueries
import com.redridgeapps.callrecorder.di.modules.android.AndroidComponentBuilder
import com.redridgeapps.callrecorder.di.modules.android.UIInitializerModule
import com.redridgeapps.repository.RecordingItem
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.AndroidInjectionModule

@Module(
    includes = [
        AndroidInjectionModule::class,
        AndroidComponentBuilder::class,
        UIInitializerModule::class,
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
        fun provideContentResolver(context: Context): ContentResolver {
            return context.contentResolver
        }

        @Provides
        fun provideRecordingList(recordingQueries: RecordingQueries): List<RecordingItem> {
            return recordingQueries.getAll()
                .executeAsList()
                .map {
                    RecordingItem(
                        name = it.name,
                        type = it.number
                    )
                }
        }
    }
}
