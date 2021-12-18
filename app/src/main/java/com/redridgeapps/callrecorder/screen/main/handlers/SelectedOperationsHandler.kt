package com.redridgeapps.callrecorder.screen.main.handlers

import com.redridgeapps.callrecorder.screen.common.utils.ClickSelection
import com.redridgeapps.callrecorder.screen.main.SelectedRecording
import com.redridgeapps.callutils.callevents.CallDirection
import com.redridgeapps.callutils.services.AudioEndsTrimmingServiceLauncher
import com.redridgeapps.callutils.services.Mp3ConversionServiceLauncher
import com.redridgeapps.callutils.storage.Recordings
import com.redridgeapps.common.utils.humanReadableByteCount
import com.redridgeapps.common.utils.launchUnit
import com.redridgeapps.common.viewmodel.ViewModelHandle
import kotlinx.coroutines.flow.first
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.nio.file.Paths
import java.time.format.DateTimeFormatter

internal class SelectedOperationsHandler(
    private val viewModelHandle: ViewModelHandle,
    private val recordingSelection: ClickSelection<SelectedRecording>,
    private val recordings: Recordings,
    private val mp3ConversionServiceLauncher: Mp3ConversionServiceLauncher,
    private val audioEndsTrimmingServiceLauncher: AudioEndsTrimmingServiceLauncher,
) {

    internal fun onDeleteRecordings() = viewModelHandle.coroutineScope.launchUnit {

        val selection = recordingSelection.state.value.selection

        recordings.deleteRecording(selection.map { it.id })
        recordingSelection.clear()
    }

    internal fun onToggleStar(newValue: Boolean) = viewModelHandle.coroutineScope.launchUnit {

        val selection = recordingSelection.state.value.selection

        recordings.setIsStarred(newValue, selection.map { it.id })
    }

    internal fun onToggleSkipAutoDelete(
        newValue: Boolean,
    ) = viewModelHandle.coroutineScope.launchUnit {

        val selection = recordingSelection.state.value.selection

        recordings.setSkipAutoDelete(newValue, selection.map { it.id })
    }

    internal fun onTrimSilenceEnds() = viewModelHandle.coroutineScope.launchUnit {

        val selection = recordingSelection.state.value.selection.map { it.id }

        audioEndsTrimmingServiceLauncher.launch(selection)
        recordingSelection.clear()
    }

    internal fun onConvertToMp3() = viewModelHandle.coroutineScope.launchUnit {

        val selection = recordingSelection.state.value.selection.map { it.id }

        mp3ConversionServiceLauncher.launch(selection)
        recordingSelection.clear()
    }

    internal suspend fun getInfoMap(): Map<String, String> = buildMap {

        val selection = recordingSelection.state.value.selection.single().id
        val recording = recordings.getRecording(selection).first()
        val wavData = recordings.getWavData(selection)

        val saveFile = Paths.get(recording.save_path).fileName.toString()
        put("File Name: ", saveFile)

        put("Contact Name: ", recording.name)
        put("Number: ", recording.number)

        val formattedStartTime = fullDateFormatter.format(
            recording.call_instant
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .toJavaLocalDateTime()
        )
        put("Recording Started: ", formattedStartTime)

        val durationSeconds = recording.call_duration.inSeconds.toLong()
        val durationText = "%d:%02d:%02d".format(
            durationSeconds / 3600,
            (durationSeconds % 3600) / 60,
            (durationSeconds % 60)
        )

        put("Duration: ", durationText)

        val direction = when (recording.call_direction) {
            CallDirection.INCOMING -> "Incoming"
            CallDirection.OUTGOING -> "Outgoing"
        }
        put("Direction: ", direction)

        put("Sample Rate: ", "${wavData.sampleRate.value} Hz")
        put("Channels: ", if (wavData.channels.value == 1) "Mono" else "Stereo")
        put("Bits per sample: ", wavData.bitsPerSample.value.toString())
        put("Bitrate: ", "${wavData.bitRate.toLong()} kb/s")
        put("File Size: ", humanReadableByteCount(wavData.fileSize.toLong()))
    }
}

private val fullDateFormatter = DateTimeFormatter.ofPattern("MMMM d, uuuu, HH:mm:ss")