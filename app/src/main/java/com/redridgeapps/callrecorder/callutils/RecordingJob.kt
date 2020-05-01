package com.redridgeapps.callrecorder.callutils

import android.content.Context
import com.redridgeapps.callrecorder.utils.prefs.PREF_AUDIO_RECORD_CHANNELS
import com.redridgeapps.callrecorder.utils.prefs.PREF_AUDIO_RECORD_ENCODING
import com.redridgeapps.callrecorder.utils.prefs.PREF_AUDIO_RECORD_SAMPLE_RATE
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
    val phoneNumber: String,
    val callDirection: CallDirection
)

@Suppress("FunctionName")
suspend fun RecordingJob(
    context: Context,
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
            context = context,
            fileName = recordingStartInstant.epochSecond.toString()
        ),
        phoneNumber = phoneNumber,
        callDirection = callDirection
    )
}
