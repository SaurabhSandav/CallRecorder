package com.redridgeapps.ui.main

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.redridgeapps.callrecorder.callutils.callevents.CallDirection
import com.redridgeapps.callrecorder.callutils.db.Recording
import com.redridgeapps.callrecorder.callutils.storage.Recordings
import com.redridgeapps.callrecorder.common.utils.toLocalDate
import com.redridgeapps.callrecorder.common.utils.toLocalDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import java.time.Duration
import java.time.format.DateTimeFormatter
import java.util.*

internal class MainViewModel @ViewModelInject constructor(
    private val recordings: Recordings
) : ViewModel() {

    private val recordingListFilter = MutableStateFlow(enumSetNoneOf<RecordingListFilter>())

    init {
        observeRecordingList()
    }

    val uiState = MainState(recordingListFilter)

    fun toggleRecordingListFilter(filter: RecordingListFilter) {

        val filterSet = EnumSet.copyOf(recordingListFilter.value)

        when (filter) {
            in filterSet -> filterSet.remove(filter)
            else -> filterSet.add(filter)
        }

        recordingListFilter.value = filterSet
    }

    fun clearRecordingListFilters() {
        recordingListFilter.value = enumSetNoneOf()
    }

    private fun observeRecordingList() {

        recordings.getRecordingList()
            .map {
                uiState.isRefreshing = true
                it.asRecordingMapByDate()
            }
            .combine(recordingListFilter) { recordingMapByDate, filterSet ->

                val filteredMap = filterRecordingMap(recordingMapByDate, filterSet)

                uiState.recordingList = filteredMap.flatMap { listOf(it.key) + it.value }
                uiState.isRefreshing = false
            }
            .launchIn(viewModelScope)
    }

    private fun filterRecordingMap(
        recordingMapByDate: Map<RecordingListItem.Divider, List<RecordingListItem.Entry>>,
        filterSet: EnumSet<RecordingListFilter>
    ): Map<RecordingListItem.Divider, List<RecordingListItem.Entry>> = when {
        filterSet.isEmpty() -> recordingMapByDate
        else -> {
            recordingMapByDate.mapValues { entry ->
                entry.value.filter { recording -> recording.applicableFilters.any { it in filterSet } }
            }.filter { entry -> entry.value.isNotEmpty() }
        }
    }

    private fun List<Recording>.asRecordingMapByDate(): Map<RecordingListItem.Divider, List<RecordingListItem.Entry>> {
        return groupBy { it.start_instant.toLocalDate() }
            .mapKeys { RecordingListItem.Divider(it.key.format(newDayFormatter)) }
            .mapValues { entry ->
                entry.value.map {

                    // Format call Interval
                    val startTime = it.start_instant.toLocalDateTime().format(overlineFormatter)
                    val overlineText = "$startTime • ${it.call_direction}"

                    val metaText = it.duration.getFormatted()

                    // Collect applicable filters
                    val filterSet = enumSetNoneOf<RecordingListFilter>()

                    when (it.call_direction) {
                        CallDirection.INCOMING -> filterSet.add(RecordingListFilter.Incoming)
                        CallDirection.OUTGOING -> filterSet.add(RecordingListFilter.Outgoing)
                    }

                    if (it.is_starred)
                        filterSet.add(RecordingListFilter.Starred)

                    // Mapped recording entry
                    RecordingListItem.Entry(
                        id = it.id,
                        name = "${it.id} - ${it.name}",
                        number = it.number,
                        overlineText = overlineText,
                        metaText = metaText,
                        applicableFilters = filterSet
                    )
                }
            }
    }
}

private val overlineFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
private val newDayFormatter = DateTimeFormatter.ofPattern("MMMM d, uuuu")

private fun Duration.getFormatted(): String =
    "%d:%02d:%02d".format(seconds / 3600, (seconds % 3600) / 60, (seconds % 60))

private inline fun <reified T : Enum<T>> enumSetNoneOf(): EnumSet<T> {
    return EnumSet.noneOf(T::class.java)
}
