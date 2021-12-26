package com.redridgeapps.callrecorder.screen.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.flow.StateFlow

@Stable
internal data class SettingsState(
    val isAppSystemized: Preference<Boolean>,

    val recordingEnabled: Preference<Boolean>,

    val onUpdateContactNames: OnUpdateContactNames,

    val audioRecordSampleRate: Preference<Int>,
    val audioRecordChannels: Preference<Int>,
    val audioRecordEncoding: Preference<Int>,

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
