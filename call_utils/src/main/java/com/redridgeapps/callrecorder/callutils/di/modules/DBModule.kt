package com.redridgeapps.callrecorder.callutils.di.modules

import android.content.Context
import com.redridgeapps.callrecorder.callutils.db.CallRecordingDB
import com.redridgeapps.callrecorder.callutils.db.Recording
import com.redridgeapps.callrecorder.callutils.db.RecordingQueries
import com.redridgeapps.callrecorder.common.db.adapters.DurationLongColumnAdapter
import com.redridgeapps.callrecorder.common.db.adapters.InstantIntegerColumnAdapter
import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object DBModule {

    @Provides
    @Singleton
    fun @receiver:ApplicationContext Context.provideCallRecordingDB(): CallRecordingDB {

        val schema = CallRecordingDB.Schema
        val driver = AndroidSqliteDriver(schema, this, "AppDB.db")

        return CallRecordingDB(
            driver = driver,
            RecordingAdapter = Recording.Adapter(
                start_instantAdapter = InstantIntegerColumnAdapter,
                durationAdapter = DurationLongColumnAdapter,
                call_directionAdapter = EnumColumnAdapter()
            )
        )
    }

    @Provides
    fun CallRecordingDB.provideRecordingQueries(): RecordingQueries = recordingQueries
}
