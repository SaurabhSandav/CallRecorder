package com.redridgeapps.callrecorder.callutils

import android.media.AudioRecord
import android.media.MediaRecorder.AudioSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CallRecorder @Inject constructor(recordings: Recordings) {

    private val _recordingState = BroadcastChannel<RecordingState>(CONFLATED)

    val recordingState: Flow<RecordingState> = _recordingState.asFlow()

    init {
        _recordingState.offer(RecordingState.Idle(recordings, _recordingState))
    }
}


sealed class RecordingState(protected val recordings: Recordings) {

    class Idle(
        recordings: Recordings,
        private val recordingState: BroadcastChannel<RecordingState>
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

            val isRecording =
                IsRecording(recordings, recorder, recordingJob, audioWriter, recordingState)
            recordingState.offer(isRecording)
        }
    }

    class IsRecording(
        recordings: Recordings,
        private val recorder: AudioRecord,
        private val recordingJob: RecordingJob,
        private val audioWriter: AudioWriter,
        private val recordingState: BroadcastChannel<RecordingState>
    ) : RecordingState(recordings) {

        suspend fun stopRecording() {

            recorder.stop()

            audioWriter.awaitWritingFinished()

            recorder.release()

            recordings.saveRecording(recordingJob)

            recordingState.offer(Idle(recordings, recordingState))
        }
    }
}

private const val BUFFER_ADDER = 4096
