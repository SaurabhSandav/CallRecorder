package com.redridgeapps.callutils.recording

import com.redridgeapps.callutils.Defaults
import com.redridgeapps.callutils.callevents.NewCallEvent
import com.redridgeapps.callutils.storage.Recordings
import com.redridgeapps.prefs.PREF_AUDIO_RECORD_CHANNELS
import com.redridgeapps.prefs.PREF_AUDIO_RECORD_ENCODING
import com.redridgeapps.prefs.PREF_AUDIO_RECORD_SAMPLE_RATE
import com.redridgeapps.prefs.PREF_RECORDINGS_STORAGE_PATH
import com.redridgeapps.prefs.Prefs
import com.redridgeapps.prefs.enum
import kotlinx.coroutines.async
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
    prefs: Prefs,
    callEvent: NewCallEvent,
): RecordingJob = coroutineScope {

    val sampleRate = async {
        prefs.enum(PREF_AUDIO_RECORD_SAMPLE_RATE) { Defaults.AUDIO_RECORD_SAMPLE_RATE }.first()
    }
    val audioChannel = async {
        prefs.enum(PREF_AUDIO_RECORD_CHANNELS) { Defaults.AUDIO_RECORD_CHANNELS }.first()
    }
    val audioEncoding = async {
        prefs.enum(PREF_AUDIO_RECORD_ENCODING) { Defaults.AUDIO_RECORD_ENCODING }.first()
    }
    val recordingsStoragePath = async {
        prefs.string(PREF_RECORDINGS_STORAGE_PATH) { error("Recordings storage path is empty") }
            .first()
    }

    val recordingStartInstant = Clock.System.now()

    val savePath = Recordings.generateFilePath(
        saveDir = recordingsStoragePath.await(),
        fileName = recordingStartInstant.epochSeconds.toString()
    )

    RecordingJob(
        pcmSampleRate = sampleRate.await(),
        pcmChannels = audioChannel.await(),
        pcmEncoding = audioEncoding.await(),
        savePath = savePath,
        newCallEvent = callEvent,
        recordingStartInstant = recordingStartInstant
    )
}
