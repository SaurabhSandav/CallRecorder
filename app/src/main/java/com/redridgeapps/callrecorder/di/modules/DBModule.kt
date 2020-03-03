package com.redridgeapps.callrecorder.di.modules

import android.content.Context
import com.redridgeapps.callrecorder.CallRecordingDB
import com.redridgeapps.callrecorder.RecordingQueries
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

        return CallRecordingDB(driver)
    }

    @Provides
    fun provideRecordingQueries(callRecordingDB: CallRecordingDB): RecordingQueries {
        return callRecordingDB.recordingQueries
    }
}
