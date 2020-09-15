package com.redridgeapps.prefs

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.createDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object PrefsModule {

    @Provides
    @Singleton
    fun providePrefsDataStore(@ApplicationContext context: Context): DataStore<Prefs> {
        return context.createDataStore(
            fileName = "preferences",
            serializer = PrefsSerializer,
        )
    }
}
