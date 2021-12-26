package com.redridgeapps.common

object PrefKeys {

    const val isInitialConfigFinished = "isInitialConfigFinished"
    const val isRecordingEnabled = "isRecordingEnabled"
    const val recordingStoragePath = "recordingStoragePath"
    const val isAutoDeleteEnabled = "isAutoDeleteEnabled"
    const val autoDeleteThresholdDays = "autoDeleteThresholdDays"

    object AudioRecord {
        const val sampleRate = "AudioRecord.sampleRate"
        const val channels = "AudioRecord.channels"
        const val encoding = "AudioRecord.encoding"
    }
}
