package com.redridgeapps.callrecorder.callutils.recording

import com.redridgeapps.callrecorder.callutils.callevents.NewCallEvent
import com.redridgeapps.callrecorder.callutils.storage.Recordings
import com.redridgeapps.callrecorder.prefs.MyPrefs
import com.redridgeapps.callrecorder.prefs.Prefs
import com.redridgeapps.callrecorder.utils.constants.Defaults
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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
        prefs.get(MyPrefs.AUDIO_RECORD_SAMPLE_RATE) { Defaults.AUDIO_RECORD_SAMPLE_RATE }
    }
    val audioChannel = async {
        prefs.get(MyPrefs.AUDIO_RECORD_CHANNELS) { Defaults.AUDIO_RECORD_CHANNELS }
    }
    val audioEncoding = async {
        prefs.get(MyPrefs.AUDIO_RECORD_ENCODING) { Defaults.AUDIO_RECORD_ENCODING }
    }
    val recordingsStoragePath = async {
        prefs.get<String>(MyPrefs.RECORDINGS_STORAGE_PATH) { error("Recordings storage path is empty") }
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
