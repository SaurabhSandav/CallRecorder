package com.redridgeapps.callrecorder.di.modules

import android.content.Context
import com.redridgeapps.callrecorder.CallRecordingDB
import com.redridgeapps.callrecorder.Recording
import com.redridgeapps.callrecorder.RecordingQueries
import com.redridgeapps.callrecorder.db.DurationLongColumnAdapter
import com.redridgeapps.callrecorder.db.InstantIntegerColumnAdapter
import com.squareup.sqldelight.EnumColumnAdapter
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DBModule {

    @Provides
    @Singleton
    fun Context.provideCallRecordingDB(): CallRecordingDB {

        val schema = CallRecordingDB.Schema
        val driver = AndroidSqliteDriver(schema, this, "AppDB.db")

        return CallRecordingDB(
            driver,
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
