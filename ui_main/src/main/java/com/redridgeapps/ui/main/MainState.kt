package com.redridgeapps.ui.main

import androidx.compose.getValue
import androidx.compose.mutableStateOf
import androidx.compose.setValue
import com.redridgeapps.ui.common.utils.ListSelection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import java.util.*

internal class MainState(
    val recordingListFilter: StateFlow<EnumSet<RecordingListFilter>>,
    val recordingAutoDeleteEnabled: Flow<Boolean>
) {

    var isRefreshing: Boolean by mutableStateOf(true)

    var recordingList: List<RecordingListItem> by mutableStateOf(emptyList())

    val selection: ListSelection<Long> = ListSelection()
}

internal sealed class RecordingListItem {

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

internal enum class RecordingListFilter {
    Incoming,
    Outgoing,
    Starred;
}

internal fun RecordingListFilter.toReadableString(): String = name

internal enum class OptionsDialogTab {
    OPTIONS,
    INFO
}

internal fun OptionsDialogTab.toReadableString(): String = name
