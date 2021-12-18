package com.redridgeapps.callrecorder.screen.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import com.redridgeapps.prefs.Prefs
import kotlinx.coroutines.flow.StateFlow

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
    val value: StateFlow<T>,
    val onChanged: (T) -> Unit,
)

@Composable
internal fun <T> Preference<T>.collectPrefValue(): T = value.collectAsState().value

internal typealias OnUpdateContactNames = () -> Unit
