package com.redridgeapps.callutils.di.modules

import android.content.Context
import com.redridgeapps.callutils.db.CallRecordingDB
import com.redridgeapps.callutils.db.Recording
import com.redridgeapps.callutils.db.RecordingIdColumnAdapter
import com.redridgeapps.callutils.db.RecordingQueries
import com.redridgeapps.common.db.adapters.DurationLongColumnAdapter
import com.redridgeapps.common.db.adapters.InstantIntegerColumnAdapter
import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DBModule {

    @Provides
    @Singleton
    fun @receiver:ApplicationContext Context.provideCallRecordingDB(): CallRecordingDB {

        val schema = CallRecordingDB.Schema
        val driver = AndroidSqliteDriver(schema, this, "AppDB.db")

        return CallRecordingDB(
            driver = driver,
            RecordingAdapter = Recording.Adapter(
                idAdapter = RecordingIdColumnAdapter,
                call_instantAdapter = InstantIntegerColumnAdapter,
                call_durationAdapter = DurationLongColumnAdapter,
                call_directionAdapter = EnumColumnAdapter()
            )
        )
    }

    @Provides
    fun CallRecordingDB.provideRecordingQueries(): RecordingQueries = recordingQueries
}
