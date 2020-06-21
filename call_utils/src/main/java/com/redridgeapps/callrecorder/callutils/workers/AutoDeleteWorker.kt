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
import com.redridgeapps.callrecorder.prefs.PREF_RECORDING_AUTO_DELETE_AFTER_DAYS
import com.redridgeapps.callrecorder.prefs.Prefs
import kotlinx.coroutines.flow.first
import java.time.Duration

class RecordingAutoDeleteWorker @WorkerInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val recordings: Recordings,
    private val prefs: Prefs
) : CoroutineWorker(appContext, workerParameters) {

    override suspend fun doWork(): Result {

        val days = prefs.prefInt(PREF_RECORDING_AUTO_DELETE_AFTER_DAYS) {
            Defaults.RECORDING_AUTO_DELETE_AFTER_DAYS
        }.first()

        recordings.deleteOverDaysOld(Duration.ofDays(days.toLong()))

        return Result.success()
    }

    companion object {

        fun schedule(context: Context) {

            val workRequest =
                PeriodicWorkRequestBuilder<RecordingAutoDeleteWorker>(Duration.ofDays(1)).build()

            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }
}
