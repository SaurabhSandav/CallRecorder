package com.redridgeapps.callutils.workers

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.redridgeapps.callutils.storage.Recordings
import com.redridgeapps.common.StartupInitializer
import com.redridgeapps.prefs.Prefs
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

@HiltWorker
class RecordingAutoDeleteWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val recordings: Recordings,
    private val prefs: DataStore<Prefs>,
) : CoroutineWorker(appContext, workerParameters) {

    override suspend fun doWork(): Result {

        val thresholdDays = prefs.data.first().auto_delete_threshold_days

        recordings.deleteAutoIfOlderThan(thresholdDays.days)

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
