package com.redridgeapps.ui.main.handlers

import com.redridgeapps.callutils.callevents.CallDirection
import com.redridgeapps.callutils.db.Recording
import com.redridgeapps.callutils.db.RecordingId
import com.redridgeapps.callutils.playback.PlaybackState
import com.redridgeapps.callutils.playback.PlaybackState.Started
import com.redridgeapps.callutils.playback.PlaybackState.Started.Playing
import com.redridgeapps.common.utils.format
import com.redridgeapps.ui.main.RecordingListEntry
import com.redridgeapps.ui.main.RecordingListEntry.Header
import com.redridgeapps.ui.main.RecordingListEntry.Item
import com.redridgeapps.ui.main.RecordingListFilter
import com.redridgeapps.ui.main.SelectedRecording
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

internal fun Flow<List<Recording>>.filterWith(
    filterFlow: Flow<EnumSet<RecordingListFilter>>,
): Flow<List<Recording>> = combine(filterFlow) { recordingList, filters ->

    if (filters.isEmpty()) return@combine recordingList

    recordingList.filter { recording ->

        val applicableFilters = buildList {

            when (recording.call_direction) {
                CallDirection.INCOMING -> add(RecordingListFilter.INCOMING)
                CallDirection.OUTGOING -> add(RecordingListFilter.OUTGOING)
            }

            if (recording.is_starred)
                add(RecordingListFilter.STARRED)
        }

        applicableFilters.any { it in filters }
    }
}

internal fun Flow<List<Recording>>.mapToRecordingListEntries(
    onSelect: (recordingId: RecordingId) -> Unit,
    onMultiSelect: (recordingId: RecordingId) -> Unit,
    onPlayPauseToggle: (recordingId: RecordingId) -> Unit,
): Flow<List<RecordingListEntry>> = map { recordings ->
    recordings.sortedByDescending { it.call_instant }
        .groupBy { it.call_instant.toLocalDateTime(TimeZone.currentSystemDefault()).date }
        .mapKeys { Header(it.key.format(newDayFormatter)) }
        .mapValues {
            it.value.mapRecordingToRecordingListEntryItem(
                onSelect = onSelect,
                onMultiSelect = onMultiSelect,
                onPlayPauseToggle = onPlayPauseToggle
            )
        }
        .flatMap { listOf(it.key) + it.value }
}

private fun List<Recording>.mapRecordingToRecordingListEntryItem(
    onSelect: (recordingId: RecordingId) -> Unit,
    onMultiSelect: (recordingId: RecordingId) -> Unit,
    onPlayPauseToggle: (recordingId: RecordingId) -> Unit,
): List<Item> = map {

    // Format call Interval
    val startTime = it.call_instant
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .format(DateTimeFormatter.ISO_LOCAL_TIME)
    val overlineText = "$startTime • ${it.call_direction}"

    val seconds = it.call_duration.inSeconds.toLong()
    val metaText = "%d:%02d:%02d"
        .format(seconds / 3600, (seconds % 3600) / 60, (seconds % 60))

    // Collect applicable filters
    val filterSet = EnumSet.noneOf(RecordingListFilter::class.java)

    when (it.call_direction) {
        CallDirection.INCOMING -> filterSet.add(RecordingListFilter.INCOMING)
        CallDirection.OUTGOING -> filterSet.add(RecordingListFilter.OUTGOING)
    }

    if (it.is_starred)
        filterSet.add(RecordingListFilter.STARRED)

    // Mapped recording entry
    Item(
        id = it.id,
        name = it.name,
        number = it.number,
        overlineText = overlineText,
        metaText = metaText,
        applicableFilters = filterSet,
        onSelect = { onSelect(it.id) },
        onMultiSelect = { onMultiSelect(it.id) },
        onPlayPauseToggle = { onPlayPauseToggle(it.id) }
    )
}

internal fun Flow<List<RecordingListEntry>>.updateWithPlaybackState(
    playbackStateFlow: Flow<PlaybackState>,
) = combine(playbackStateFlow) { entry, playbackState ->
    entry.map {
        when (it) {
            is Item -> it.copy(
                isStarted = playbackState is Started && playbackState.recordingId == it.id,
                isPlaying = playbackState is Playing && playbackState.recordingId == it.id,
            )
            else -> it
        }
    }
}

internal fun Flow<List<RecordingListEntry>>.updateWithSelection(
    selectionFlow: Flow<List<SelectedRecording>>,
) = combine(selectionFlow) { entry, selection ->

    val selectedIds = selection.map { it.id }

    entry.map {
        when (it) {
            is Item -> it.copy(isSelected = it.id in selectedIds)
            else -> it
        }
    }
}

private val newDayFormatter = DateTimeFormatter.ofPattern("MMMM d, uuuu")
