package com.redridgeapps.callrecorder.ui.main

import androidx.compose.getValue
import androidx.compose.mutableStateOf
import androidx.compose.setValue
import com.redridgeapps.callrecorder.callutils.playback.PlaybackState
import com.redridgeapps.callrecorder.ui.utils.ListSelection
import kotlinx.coroutines.flow.StateFlow
import java.util.*

class MainState(
    val playbackState: StateFlow<PlaybackState>,
    val recordingListFilter: StateFlow<EnumSet<RecordingListFilter>>
) {

    var isRefreshing: Boolean by mutableStateOf(true)

    var recordingList: List<RecordingListItem> by mutableStateOf(emptyList())

    val selection: ListSelection<RecordingListItem.Entry> = ListSelection()
}

sealed class RecordingListItem {

    class Divider(val title: String) : RecordingListItem()

    class Entry(
        val id: Long,
        val name: String,
        val number: String,
        val overlineText: String,
        val metaText: String,
        val applicableFilters: Set<RecordingListFilter>
    ) : RecordingListItem()
}

enum class RecordingListFilter {
    Incoming,
    Outgoing,
    Starred;
}

fun RecordingListFilter.toReadableString(): String = name

enum class OptionsDialogTab {
    OPTIONS,
    INFO
}

fun OptionsDialogTab.toReadableString(): String = name
