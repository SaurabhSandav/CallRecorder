package com.redridgeapps.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import com.redridgeapps.callutils.recording.PcmChannels
import com.redridgeapps.callutils.recording.PcmEncoding
import com.redridgeapps.callutils.recording.PcmSampleRate
import kotlinx.coroutines.flow.Flow

@Stable
internal data class SettingsState(
    val isAppSystemized: Preference<Boolean>,

    val recordingEnabled: Preference<Boolean>,

    val onUpdateContactNames: OnUpdateContactNames,

    val audioRecordSampleRate: Preference<PcmSampleRate>,
    val audioRecordChannels: Preference<PcmChannels>,
    val audioRecordEncoding: Preference<PcmEncoding>,

    val autoDeleteEnabled: Preference<Boolean>,
    val autoDeleteAfterDays: Preference<Int>,
)

@Stable
internal data class Preference<T>(
    val value: Flow<T>,
    val initialValue: T,
    val onChanged: (T) -> Unit,
)

@Composable
internal fun <T> Preference<T>.collectPrefValue(): T {
    return value.collectAsState(initial = initialValue).value
}

internal typealias OnUpdateContactNames = () -> Unit
