package com.redridgeapps.callrecorder.callutils

import android.media.AudioRecord
import android.media.MediaRecorder.AudioSource
import android.os.PowerManager
import com.redridgeapps.callrecorder.utils.ToastMaker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CallRecorder @Inject constructor(
    powerManager: PowerManager,
    recordings: Recordings,
    private val toastMaker: ToastMaker
) {

    private val _recordingState =
        MutableStateFlow<RecordingState>(RecordingState.UnInitialized(recordings))

    val recordingState: Flow<RecordingState> = _recordingState.onEach { recordingStateChanged(it) }

    init {
        _recordingState.value = RecordingState.NotRecording(recordings, _recordingState)
    }

    private val wakeLock = powerManager.newWakeLock(
        PowerManager.PARTIAL_WAKE_LOCK,
        javaClass.simpleName
    )

    private fun recordingStateChanged(recordingState: RecordingState) {
        when (recordingState) {
            is RecordingState.NotRecording -> {
                //noinspection WakelockTimeout
                wakeLock.acquire()
                toastMaker.newToast("Started recording").show()
            }
            is RecordingState.IsRecording -> {

                if (wakeLock.isHeld)
                    wakeLock.release()

                toastMaker.newToast("Stopped recording").show()
            }
            else -> Unit
        }
    }
}


sealed class RecordingState(protected val recordings: Recordings) {

    class UnInitialized(recordings: Recordings) : RecordingState(recordings)

    class NotRecording(
        recordings: Recordings,
        private val recordingState: MutableStateFlow<RecordingState>
    ) : RecordingState(recordings) {

        suspend fun startRecording(
            recordingJob: RecordingJob,
            audioWriter: AudioWriter
        ) = withContext(Dispatchers.IO) {

            val sampleRate = recordingJob.pcmSampleRate.sampleRate
            val channel = recordingJob.pcmChannels.toAudioRecordChannel()
            val encoding = recordingJob.pcmEncoding.toAudioRecordEncoding()

            val bufferSize =
                AudioRecord.getMinBufferSize(sampleRate, channel, encoding) + BUFFER_ADDER

            val recorder =
                AudioRecord(AudioSource.VOICE_CALL, sampleRate, channel, encoding, bufferSize)
            recorder.startRecording()

            audioWriter.startWriting(recorder, recordingJob, bufferSize)

            recordingState.value =
                IsRecording(recordings, recorder, recordingJob, audioWriter, recordingState)
        }
    }

    class IsRecording(
        recordings: Recordings,
        private val recorder: AudioRecord,
        private val recordingJob: RecordingJob,
        private val audioWriter: AudioWriter,
        private val recordingState: MutableStateFlow<RecordingState>
    ) : RecordingState(recordings) {

        suspend fun stopRecording() {

            recorder.stop()

            audioWriter.awaitWritingFinished()

            recorder.release()

            recordings.saveRecording(recordingJob)

            recordingState.value = NotRecording(recordings, recordingState)
        }
    }
}

private const val BUFFER_ADDER = 4096
