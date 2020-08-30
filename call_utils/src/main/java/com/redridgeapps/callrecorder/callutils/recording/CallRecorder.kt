package com.redridgeapps.callrecorder.callutils.recording

import android.media.AudioRecord
import android.media.MediaRecorder.AudioSource
import com.redridgeapps.callrecorder.callutils.storage.Recordings
import com.redridgeapps.callrecorder.common.AppDispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CallRecorder @Inject constructor(
    private val recordings: Recordings,
    private val dispatchers: AppDispatchers,
) {

    private val _recordingState = MutableStateFlow<RecordingState>(RecordingState.Idle)

    val recordingState: StateFlow<RecordingState> = _recordingState

    suspend fun @receiver:Suppress("unused") RecordingState.Idle.startRecording(
        recordingJob: RecordingJob,
        audioWriter: AudioWriter,
    ) = withContext(dispatchers.IO) {

        val sampleRate = recordingJob.pcmSampleRate.sampleRate
        val channel = recordingJob.pcmChannels.toAudioRecordChannel()
        val encoding = recordingJob.pcmEncoding.toAudioRecordEncoding()

        val bufferSize =
            AudioRecord.getMinBufferSize(sampleRate, channel, encoding) + BUFFER_ADDER

        val recorder =
            AudioRecord(AudioSource.VOICE_CALL, sampleRate, channel, encoding, bufferSize)
        recorder.startRecording()

        audioWriter.startWriting(recorder, recordingJob, bufferSize)

        val isRecording = RecordingState.IsRecording(recorder, recordingJob, audioWriter)

        _recordingState.value = isRecording
    }

    suspend fun RecordingState.IsRecording.stopRecording() {

        recorder.stop()

        audioWriter.awaitWritingFinished()

        recorder.release()

        recordings.saveRecording(recordingJob)

        _recordingState.value = RecordingState.Idle
    }
}

sealed class RecordingState {

    object Idle : RecordingState()

    class IsRecording(
        val recorder: AudioRecord,
        val recordingJob: RecordingJob,
        val audioWriter: AudioWriter,
    ) : RecordingState()
}

private const val BUFFER_ADDER = 4096
