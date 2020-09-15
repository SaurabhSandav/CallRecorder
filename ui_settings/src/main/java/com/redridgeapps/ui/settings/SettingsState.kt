package com.redridgeapps.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import com.redridgeapps.prefs.Prefs
import kotlinx.coroutines.flow.Flow

@Stable
internal data class SettingsState(
    val isAppSystemized: Preference<Boolean>,

    val recordingEnabled: Preference<Boolean>,

    val onUpdateContactNames: OnUpdateContactNames,

    val audioRecordSampleRate: Preference<Prefs.AudioRecord.SampleRate>,
    val audioRecordChannels: Preference<Prefs.AudioRecord.Channels>,
    val audioRecordEncoding: Preference<Prefs.AudioRecord.Encoding>,

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
