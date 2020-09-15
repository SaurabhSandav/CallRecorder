package com.redridgeapps.callutils.recording

import androidx.datastore.DataStore
import com.redridgeapps.callutils.callevents.NewCallEvent
import com.redridgeapps.callutils.storage.Recordings
import com.redridgeapps.callutils.toPcmChannels
import com.redridgeapps.callutils.toPcmEncoding
import com.redridgeapps.callutils.toPcmSampleRate
import com.redridgeapps.prefs.Prefs
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.nio.file.Path

data class RecordingJob internal constructor(
    val pcmSampleRate: PcmSampleRate,
    val pcmChannels: PcmChannels,
    val pcmEncoding: PcmEncoding,
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

    val pcmSampleRate = audioRecord.sample_rate.toPcmSampleRate()
    val pcmChannels = audioRecord.channels.toPcmChannels()
    val pcmEncoding = audioRecord.encoding.toPcmEncoding()
    val recordingsStoragePath = prefsData.recording_storage_path.ifBlank {
        error("Recordings storage path is empty")
    }

    val recordingStartInstant = Clock.System.now()

    val savePath = Recordings.generateFilePath(
        saveDir = recordingsStoragePath,
        fileName = recordingStartInstant.epochSeconds.toString()
    )

    RecordingJob(
        pcmSampleRate = pcmSampleRate,
        pcmChannels = pcmChannels,
        pcmEncoding = pcmEncoding,
        savePath = savePath,
        newCallEvent = callEvent,
        recordingStartInstant = recordingStartInstant
    )
}
