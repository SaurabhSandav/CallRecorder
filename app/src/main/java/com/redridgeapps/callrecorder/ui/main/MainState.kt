package com.redridgeapps.callrecorder.ui.main

import androidx.compose.getValue
import androidx.compose.mutableStateOf
import androidx.compose.setValue
import com.redridgeapps.callrecorder.callutils.PlaybackState
import com.redridgeapps.callrecorder.callutils.RecordingId
import com.redridgeapps.callrecorder.ui.utils.ListSelection
import com.redridgeapps.callrecorder.utils.enumSetComplementOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import java.util.*

class MainState(
    val playbackState: Flow<PlaybackState>,
    val recordingListFilter: StateFlow<EnumSet<RecordingListFilter>>
) {

    var isRefreshing: Boolean by mutableStateOf(true)

    var recordingList: List<RecordingListItem> by mutableStateOf(emptyList())

    val selection: ListSelection<RecordingListItem.Entry> = ListSelection()
}

sealed class RecordingListItem {

    class Divider(val title: String) : RecordingListItem()

    class Entry(
        val id: RecordingId,
        val name: String,
        val number: String,
        val overlineText: String,
        val metaText: String,
        val applicableFilters: Set<RecordingListFilter>
    ) : RecordingListItem()
}

enum class RecordingListFilter {
    All,
    Incoming,
    Outgoing,
    Starred;

    fun toReadableString(): String = name

    companion object {
        val EXCEPT_ALL: EnumSet<RecordingListFilter> = enumSetComplementOf(All)
    }
}

enum class OptionsDialogTab {
    OPTIONS,
    INFO
}

fun OptionsDialogTab.toReadableString(): String = name