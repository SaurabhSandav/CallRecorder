package com.redridgeapps.ui.main

import androidx.compose.runtime.Stable
import com.redridgeapps.callrecorder.callutils.db.RecordingId
import kotlinx.coroutines.flow.Flow
import java.util.*

@Stable
internal data class MainState(
    val recordingListState: RecordingListState = RecordingListState(),
    val filterState: FilterState,
    val selectionState: SelectionState,
    val selectedRecordingOperations: SelectedRecordingOperations,
    val autoDeleteEnabled: Boolean = false,
    val currentPlayback: CurrentPlayback? = null,
)

internal sealed class RecordingListEntry {

    @Stable
    data class Header(val title: String) : RecordingListEntry()

    @Stable
    data class Item(
        val id: RecordingId,
        val name: String,
        val number: String,
        val overlineText: String,
        val metaText: String,
        val applicableFilters: Set<RecordingListFilter>,
        val isSelected: Boolean = false,
        val isStarted: Boolean = false,
        val isPlaying: Boolean = false,
        val onSelect: () -> Unit,
        val onMultiSelect: () -> Unit,
        val onPlayPauseToggle: () -> Unit,
    ) : RecordingListEntry()
}

@Stable
internal data class RecordingListState(
    val isRecordingListRefreshing: Boolean = true,
    val recordingList: List<RecordingListEntry> = emptyList(),
)

@Stable
internal data class FilterState(
    val filters: EnumSet<RecordingListFilter> = EnumSet.noneOf(RecordingListFilter::class.java),
    val onToggleFilter: (RecordingListFilter) -> Unit,
    val onClearRecordingListFilters: () -> Unit,
)

@Stable
internal data class SelectionState(
    val selection: List<SelectedRecording> = emptyList(),
    val inMultiSelectMode: Boolean = false,
    val onCloseSelectionMode: () -> Unit,
)

@Stable
internal data class SelectedRecording(
    val id: RecordingId,
    val isStarred: Boolean,
    val skipAutoDelete: Boolean,
)

@Stable
internal data class SelectedRecordingOperations(
    val onDeleteRecordings: () -> Unit,
    val onToggleStar: (Boolean) -> Unit,
    val onToggleSkipAutoDelete: (Boolean) -> Unit,
    val onTrimSilenceEnds: () -> Unit,
    val onConvertToMp3: () -> Unit,
    val getInfoMap: suspend () -> Map<String, String>,
)

@Stable
internal data class CurrentPlayback(
    val title: String,
    val isPlaying: Boolean,
    val positionFlow: Flow<Float>,
    val onPlayPauseToggle: () -> Unit,
    val onPlaybackStop: () -> Unit,
    val onPlaybackSeek: (Float) -> Unit,
)

internal enum class RecordingListFilter {
    INCOMING,
    OUTGOING,
    STARRED;
}

internal enum class OptionsDialogTab {
    OPTIONS,
    INFO
}

internal typealias SetState = (MainState.() -> MainState) -> Unit
internal typealias OnNavigateToSettings = () -> Unit
