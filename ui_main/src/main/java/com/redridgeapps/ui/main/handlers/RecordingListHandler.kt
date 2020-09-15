package com.redridgeapps.ui.main.handlers

import com.redridgeapps.callutils.db.RecordingId
import com.redridgeapps.callutils.playback.CallPlayback
import com.redridgeapps.callutils.storage.Recordings
import com.redridgeapps.common.viewmodel.ViewModelHandle
import com.redridgeapps.ui.common.utils.ClickSelection
import com.redridgeapps.ui.main.RecordingListFilter
import com.redridgeapps.ui.main.SelectedRecording
import com.redridgeapps.ui.main.SetState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.util.*

internal class RecordingListHandler(
    private val viewModelHandle: ViewModelHandle,
    private val setState: SetState,
    private val recordingSelection: ClickSelection<SelectedRecording>,
    private val recordings: Recordings,
    private val callPlayback: CallPlayback,
    private val onRecordingSelect: (RecordingId) -> Unit,
    private val onRecordingMultiSelect: (RecordingId) -> Unit,
    private val onRecordingPlayPauseToggle: (RecordingId) -> Unit,
) {

    private val filters = MutableStateFlow(EnumSet.noneOf(RecordingListFilter::class.java))

    init {
        viewModelHandle.onInit {
            observeFilters()
            observeRecordingList()
        }
    }

    internal fun onToggleRecordingListFilter(filter: RecordingListFilter) {

        val filterSet = EnumSet.copyOf(filters.value)

        when (filter) {
            in filterSet -> filterSet.remove(filter)
            else -> filterSet.add(filter)
        }

        filters.value = filterSet
    }

    internal fun onClearRecordingListFilters() {
        filters.value = EnumSet.noneOf(RecordingListFilter::class.java)
    }

    private fun observeFilters() {
        filters.onEach {
            setState {
                copy(filterState = filterState.copy(filters = it))
            }
        }.launchIn(viewModelHandle.coroutineScope)
    }

    private fun observeRecordingList() {

        val selectionFlow = recordingSelection.state.map { it.selection }.distinctUntilChanged()

        recordings.getAllRecordings()
            .filterWith(filters)
            .mapToRecordingListEntries(
                onSelect = onRecordingSelect,
                onMultiSelect = onRecordingMultiSelect,
                onPlayPauseToggle = onRecordingPlayPauseToggle
            )
            .updateWithPlaybackState(callPlayback.playbackState)
            .updateWithSelection(selectionFlow)
            .onEach {
                setState {

                    val recordingListState = recordingListState.copy(
                        isRecordingListRefreshing = false,
                        recordingList = it,
                    )

                    copy(recordingListState = recordingListState)
                }
            }
            .launchIn(viewModelHandle.coroutineScope)
    }
}
