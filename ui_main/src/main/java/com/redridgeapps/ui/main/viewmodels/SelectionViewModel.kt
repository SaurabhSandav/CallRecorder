package com.redridgeapps.ui.main.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redridgeapps.callrecorder.callutils.Defaults
import com.redridgeapps.callrecorder.callutils.callevents.CallDirection
import com.redridgeapps.callrecorder.callutils.recording.PcmEncoding
import com.redridgeapps.callrecorder.callutils.recording.asPcmEncoding
import com.redridgeapps.callrecorder.callutils.services.AudioEndsTrimmingServiceLauncher
import com.redridgeapps.callrecorder.callutils.services.Mp3ConversionServiceLauncher
import com.redridgeapps.callrecorder.callutils.storage.Recordings
import com.redridgeapps.callrecorder.common.utils.humanReadableByteCount
import com.redridgeapps.callrecorder.common.utils.launchUnit
import com.redridgeapps.callrecorder.prefs.PREF_AUTO_DELETE_ENABLED
import com.redridgeapps.callrecorder.prefs.Prefs
import com.redridgeapps.ui.common.utils.ListSelection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.nio.file.Paths
import java.time.format.DateTimeFormatter

class SelectionViewModel @ViewModelInject constructor(
    prefs: Prefs,
    private val recordings: Recordings,
    private val mp3ConversionServiceLauncher: Mp3ConversionServiceLauncher,
    private val audioEndsTrimmingServiceLauncher: AudioEndsTrimmingServiceLauncher,
) : ViewModel() {

    val selection: ListSelection<Long> = ListSelection()
    val showSkipAutoDelete: Flow<Boolean> = prefs.boolean(PREF_AUTO_DELETE_ENABLED) {
        Defaults.AUTO_DELETE_ENABLED
    }

    fun getIsStarred(): Flow<Boolean> {
        return recordings.getIsStarred(selection.single())
    }

    fun toggleStar() = viewModelScope.launchUnit {
        recordings.toggleStar(selection.toList())
    }

    fun getSkipAutoDelete(): Flow<Boolean> {
        return recordings.getSkipAutoDelete(selection.single())
    }

    fun toggleSkipAutoDelete() = viewModelScope.launchUnit {
        recordings.toggleSkipAutoDelete(selection.toList())
    }

    fun trimSilenceEnds() = viewModelScope.launchUnit {
        audioEndsTrimmingServiceLauncher.launch(selection.toList())
        selection.clear()
    }

    fun convertToMp3() = viewModelScope.launchUnit {
        mp3ConversionServiceLauncher.launch(selection.toList())
        selection.clear()
    }

    fun deleteRecordings() = viewModelScope.launchUnit {
        recordings.deleteRecording(selection.toList())
        selection.clear()
    }

    suspend fun getInfo(): List<Pair<String, String>> {

        val selection = selection.single()
        val wavData = recordings.getWavData(selection)
        val recording = recordings.getRecording(selection).first()

        return buildList {

            val saveFile = Paths.get(recording.save_path).fileName.toString()
            add("File Name: " to saveFile)

            add("Contact Name: " to recording.name)
            add("Number: " to recording.number)

            val formattedStartTime = fullDateFormatter.format(
                recording.start_instant
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                    .toJavaLocalDateTime()
            )
            add("Recording Started: " to formattedStartTime)

            val durationSeconds = recording.duration.inSeconds.toLong()
            val durationText = "%d:%02d:%02d".format(
                durationSeconds / 3600,
                (durationSeconds % 3600) / 60,
                (durationSeconds % 60)
            )

            add("Duration: " to durationText)

            val direction = when (recording.call_direction) {
                CallDirection.INCOMING -> "Incoming"
                CallDirection.OUTGOING -> "Outgoing"
            }
            add("Direction: " to direction)

            val encoding = when (wavData.bitsPerSample.asPcmEncoding()) {
                PcmEncoding.PCM_8BIT -> "Pcm 8 bit"
                PcmEncoding.PCM_16BIT -> "Pcm 16 bit"
                PcmEncoding.PCM_FLOAT -> "Pcm 32 bit (Float)"
            }
            add("Pcm Encoding: " to encoding)

            add("Sample Rate: " to "${wavData.sampleRate.value} Hz")
            add("Channels: " to if (wavData.channels.value == 1) "Mono" else "Stereo")
            add("Bitrate: " to "${wavData.bitRate.toLong()} kb/s")
            add("File Size: " to humanReadableByteCount(wavData.fileSize.toLong()))
        }
    }
}

private val fullDateFormatter = DateTimeFormatter.ofPattern("MMMM d, uuuu, HH:mm:ss")
