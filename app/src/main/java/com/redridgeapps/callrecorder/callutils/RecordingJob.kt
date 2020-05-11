package com.redridgeapps.callrecorder.callutils

import com.redridgeapps.callrecorder.utils.prefs.*
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.nio.file.Path
import java.time.Instant

data class RecordingJob(
    val pcmSampleRate: PcmSampleRate,
    val pcmChannels: PcmChannels,
    val pcmEncoding: PcmEncoding,
    val savePath: Path,
    val phoneNumber: String,
    val callDirection: CallDirection,
    val recordingStartInstant: Instant
)

@Suppress("FunctionName")
suspend fun RecordingJob(
    prefs: Prefs,
    phoneNumber: String,
    callDirection: CallDirection
): RecordingJob = coroutineScope {

    val sampleRate = async { prefs.get(PREF_AUDIO_RECORD_SAMPLE_RATE) }
    val audioChannel = async { prefs.get(PREF_AUDIO_RECORD_CHANNELS) }
    val audioEncoding = async { prefs.get(PREF_AUDIO_RECORD_ENCODING) }

    val recordingStartInstant = Instant.now()

    RecordingJob(
        pcmSampleRate = sampleRate.await(),
        pcmChannels = audioChannel.await(),
        pcmEncoding = audioEncoding.await(),
        savePath = Recordings.generateFilePath(
            saveDir = prefs.get(PREF_RECORDING_PATH).ifEmpty { error("Recording path is empty") },
            fileName = recordingStartInstant.epochSecond.toString()
        ),
        phoneNumber = phoneNumber,
        callDirection = callDirection,
        recordingStartInstant = recordingStartInstant
    )
}
