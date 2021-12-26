package com.redridgeapps.callutils.recording

import com.redridgeapps.callutils.Defaults
import com.redridgeapps.callutils.callevents.NewCallEvent
import com.redridgeapps.callutils.storage.Recordings
import com.redridgeapps.common.PrefKeys
import com.redridgeapps.wavutils.WavBitsPerSample
import com.redridgeapps.wavutils.WavChannels
import com.redridgeapps.wavutils.WavSampleRate
import com.russhwolf.settings.coroutines.FlowSettings
import kotlinx.coroutines.coroutineScope
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
    prefs: FlowSettings,
    callEvent: NewCallEvent,
): RecordingJob = coroutineScope {

    val recordingStartInstant = Clock.System.now()

    val savePath = Recordings.generateFilePath(
        saveDir = prefs.getString(PrefKeys.recordingStoragePath)
            .ifBlank { error("Recordings storage path is empty") },
        fileName = recordingStartInstant.epochSeconds.toString()
    )

    RecordingJob(
        sampleRate = WavSampleRate(prefs.getInt(PrefKeys.AudioRecord.sampleRate,
            Defaults.AUDIO_RECORD_SAMPLE_RATE.value)),
        channels = WavChannels(prefs.getInt(PrefKeys.AudioRecord.channels, Defaults.AUDIO_RECORD_CHANNELS.value)),
        encoding = WavBitsPerSample(prefs.getInt(PrefKeys.AudioRecord.encoding, Defaults.AUDIO_RECORD_ENCODING.value)),
        savePath = savePath,
        newCallEvent = callEvent,
        recordingStartInstant = recordingStartInstant
    )
}
