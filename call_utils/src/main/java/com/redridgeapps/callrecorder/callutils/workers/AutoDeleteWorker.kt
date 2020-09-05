package com.redridgeapps.callrecorder.callutils.workers

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.redridgeapps.callrecorder.callutils.Defaults
import com.redridgeapps.callrecorder.callutils.storage.Recordings
import com.redridgeapps.callrecorder.common.StartupInitializer
import com.redridgeapps.callrecorder.prefs.PREF_AUTO_DELETE_AFTER_DAYS
import com.redridgeapps.callrecorder.prefs.Prefs
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import kotlin.time.days
import kotlin.time.toJavaDuration

class RecordingAutoDeleteWorker @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val recordings: Recordings,
    private val prefs: Prefs
) : CoroutineWorker(appContext, workerParameters) {

    override suspend fun doWork(): Result {

        val days = prefs.int(PREF_AUTO_DELETE_AFTER_DAYS) {
            Defaults.AUTO_DELETE_AFTER_DAYS
        }.first()

        recordings.deleteAutoIfOlderThan(days.days)

        return Result.success()
    }

    companion object {

        internal fun schedule(context: Context) {

            val workRequest =
                PeriodicWorkRequestBuilder<RecordingAutoDeleteWorker>(1.days.toJavaDuration()).build()

            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }

    class Initializer @Inject constructor() : StartupInitializer {

        override fun initialize(context: Context) {
            schedule(context)
        }
    }
}
