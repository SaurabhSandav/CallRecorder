package com.redridgeapps.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PrefsModule {

    private val Context.prefsDataStore: DataStore<Prefs> by dataStore(
        fileName = "preferences",
        serializer = PrefsSerializer
    )


    @Provides
    @Singleton
    fun providePrefsDataStore(@ApplicationContext context: Context): DataStore<Prefs> {
        return context.prefsDataStore
    }
}
