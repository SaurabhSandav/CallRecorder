package com.redridgeapps.callrecorder.callutils

import com.redridgeapps.callrecorder.utils.prefs.MyPrefs
import com.redridgeapps.callrecorder.utils.prefs.Prefs
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.nio.file.Path
import java.time.Instant

data class RecordingJob(
    val pcmSampleRate: PcmSampleRate,
    val pcmChannels: PcmChannels,
    val pcmEncoding: PcmEncoding,
    val savePath: Path,
    val callEvent: NewCallEvent,
    val recordingStartInstant: Instant
)

@Suppress("FunctionName")
suspend fun RecordingJob(
    prefs: Prefs,
    callEvent: NewCallEvent
): RecordingJob = coroutineScope {

    val sampleRate = async { prefs.get(MyPrefs.AUDIO_RECORD_SAMPLE_RATE) { PcmSampleRate.S44_100 } }
    val audioChannel = async { prefs.get(MyPrefs.AUDIO_RECORD_CHANNELS) { PcmChannels.MONO } }
    val audioEncoding = async { prefs.get(MyPrefs.AUDIO_RECORD_ENCODING) { PcmEncoding.PCM_16BIT } }

    val recordingStartInstant = Instant.now()

    RecordingJob(
        pcmSampleRate = sampleRate.await(),
        pcmChannels = audioChannel.await(),
        pcmEncoding = audioEncoding.await(),
        savePath = Recordings.generateFilePath(
            saveDir = prefs.get<String>(MyPrefs.RECORDING_PATH) { error("Recording path is empty") },
            fileName = recordingStartInstant.epochSecond.toString()
        ),
        callEvent = callEvent,
        recordingStartInstant = recordingStartInstant
    )
}
