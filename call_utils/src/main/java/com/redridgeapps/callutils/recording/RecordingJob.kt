package com.redridgeapps.callutils.recording

import androidx.datastore.DataStore
import com.redridgeapps.callutils.callevents.NewCallEvent
import com.redridgeapps.callutils.storage.Recordings
import com.redridgeapps.prefs.Prefs
import com.redridgeapps.wavutils.WavBitsPerSample
import com.redridgeapps.wavutils.WavChannels
import com.redridgeapps.wavutils.WavSampleRate
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.nio.file.Path

data class RecordingJob internal constructor(
    val sampleRate: WavSampleRate,
    val channels: WavChannels,
    val encoding: WavBitsPerSample,
    val savePath: Path,
    val newCallEvent: NewCallEvent,
    val recordingStartInstant: Instant,
)

@Suppress("FunctionName")
suspend fun RecordingJob(
    prefs: DataStore<Prefs>,
    callEvent: NewCallEvent,
): RecordingJob = coroutineScope {

    val prefsData = prefs.data.first()
    val audioRecord = prefsData.audio_record ?: Prefs.AudioRecord()

    val recordingStartInstant = Clock.System.now()

    val savePath = Recordings.generateFilePath(
        saveDir = prefsData.recording_storage_path.ifBlank { error("Recordings storage path is empty") },
        fileName = recordingStartInstant.epochSeconds.toString()
    )

    RecordingJob(
        sampleRate = audioRecord.sample_rate.toWavSampleRate(),
        channels = audioRecord.channels.toWavChannels(),
        encoding = audioRecord.encoding.toWavBitsPerSample(),
        savePath = savePath,
        newCallEvent = callEvent,
        recordingStartInstant = recordingStartInstant
    )
}
