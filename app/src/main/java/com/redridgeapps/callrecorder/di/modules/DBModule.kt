package com.redridgeapps.callrecorder.di.modules

import android.content.Context
import com.redridgeapps.callrecorder.CallRecordingDB
import com.redridgeapps.callrecorder.Recording
import com.redridgeapps.callrecorder.RecordingQueries
import com.redridgeapps.callrecorder.db.InstantIntegerColumnAdapter
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DBModule {

    @Provides
    @Singleton
    fun provideCallRecordingDB(context: Context): CallRecordingDB {

        val schema = CallRecordingDB.Schema
        val driver = AndroidSqliteDriver(schema, context, "AppDB.db")

        return CallRecordingDB(
            driver,
            RecordingAdapter = Recording.Adapter(
                startInstantAdapter = InstantIntegerColumnAdapter,
                endInstantAdapter = InstantIntegerColumnAdapter
            )
        )
    }

    @Provides
    fun provideRecordingQueries(callRecordingDB: CallRecordingDB): RecordingQueries {
        return callRecordingDB.recordingQueries
    }
}
