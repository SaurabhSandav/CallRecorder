package com.redridgeapps.callrecorder.callutils

import com.redridgeapps.callrecorder.callutils.callevents.NewCallEvent
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

    val sampleRate =
        async { prefs.get(MyPrefs.AUDIO_RECORD_SAMPLE_RATE) { Defaults.AUDIO_RECORD_SAMPLE_RATE } }
    val audioChannel =
        async { prefs.get(MyPrefs.AUDIO_RECORD_CHANNELS) { Defaults.AUDIO_RECORD_CHANNELS } }
    val audioEncoding =
        async { prefs.get(MyPrefs.AUDIO_RECORD_ENCODING) { Defaults.AUDIO_RECORD_ENCODING } }

    val recordingStartInstant = Instant.now()

    RecordingJob(
        pcmSampleRate = sampleRate.await(),
        pcmChannels = audioChannel.await(),
        pcmEncoding = audioEncoding.await(),
        savePath = Recordings.generateFilePath(
            saveDir = prefs.get<String>(MyPrefs.RECORDING_PATH) { error("Recording path is empty") },
            fileName = recordingStartInstant.epochSecond.toString()
        ),
        newCallEvent = callEvent,
        recordingStartInstant = recordingStartInstant
    )
}
