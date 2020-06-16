package com.redridgeapps.callrecorder.callutils.recording

import com.redridgeapps.callrecorder.callutils.callevents.NewCallEvent
import com.redridgeapps.callrecorder.callutils.storage.Recordings
import com.redridgeapps.callrecorder.prefs.*
import com.redridgeapps.callrecorder.utils.constants.Defaults
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.first
import java.nio.file.Path
import java.time.Instant

data class RecordingJob(
    val pcmSampleRate: PcmSampleRate,
    val pcmChannels: PcmChannels,
    val pcmEncoding: PcmEncoding,
    val savePath: Path,
    val newCallEvent: NewCallEvent,
    val recordingStartInstant: Instant
)

@Suppress("FunctionName")
suspend fun RecordingJob(
    prefs: Prefs,
    callEvent: NewCallEvent
): RecordingJob = coroutineScope {

    val sampleRate = async {
        prefs.prefEnum(PREF_AUDIO_RECORD_SAMPLE_RATE) { Defaults.AUDIO_RECORD_SAMPLE_RATE }.first()
    }
    val audioChannel = async {
        prefs.prefEnum(PREF_AUDIO_RECORD_CHANNELS) { Defaults.AUDIO_RECORD_CHANNELS }.first()
    }
    val audioEncoding = async {
        prefs.prefEnum(PREF_AUDIO_RECORD_ENCODING) { Defaults.AUDIO_RECORD_ENCODING }.first()
    }
    val recordingsStoragePath = async {
        prefs.prefString<String>(PREF_RECORDINGS_STORAGE_PATH) { error("Recordings storage path is empty") }
            .first()
    }

    val recordingStartInstant = Instant.now()

    val savePath = Recordings.generateFilePath(
        saveDir = recordingsStoragePath.await(),
        fileName = recordingStartInstant.epochSecond.toString()
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
