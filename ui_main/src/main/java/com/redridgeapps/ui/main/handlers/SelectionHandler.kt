package com.redridgeapps.ui.main.handlers

import androidx.datastore.DataStore
import com.redridgeapps.callutils.db.Recording
import com.redridgeapps.callutils.db.RecordingId
import com.redridgeapps.callutils.storage.Recordings
import com.redridgeapps.common.utils.launchUnit
import com.redridgeapps.common.viewmodel.ViewModelHandle
import com.redridgeapps.prefs.Prefs
import com.redridgeapps.ui.common.utils.ClickSelection
import com.redridgeapps.ui.main.SelectedRecording
import com.redridgeapps.ui.main.SetState
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

internal class SelectionHandler constructor(
    private val viewModelHandle: ViewModelHandle,
    private val setState: SetState,
    private val recordingSelection: ClickSelection<SelectedRecording>,
    private val prefs: DataStore<Prefs>,
    private val recordings: Recordings,
) {

    init {
        viewModelHandle.onInit { observeSelection() }
    }

    internal fun onSelect(id: RecordingId) = viewModelHandle.coroutineScope.launchUnit {

        val recording = recordings.getRecording(id).first()
        val selectedRecording = buildSelectedRecording(recording)

        recordingSelection.select(selectedRecording)
    }

    internal fun onMultiSelect(id: RecordingId) = viewModelHandle.coroutineScope.launchUnit {

        val recording = recordings.getRecording(id).first()
        val selectedRecording = buildSelectedRecording(recording)

        recordingSelection.multiSelect(selectedRecording)
    }

    internal fun onCloseSelectionMode() {
        recordingSelection.clear()
    }

    private fun observeSelection() {

        val autoDeleteEnabledFlow = prefs.data.map { it.is_auto_delete_enabled }

        recordingSelection.state
            .flatMapLatest { state ->
                recordings.getRecordings(state.selection.map { it.id })
                    .map { list ->
                        state.inMultiSelectMode to list.map(this::buildSelectedRecording)
                    }
            }
            .combine(autoDeleteEnabledFlow) { pair, autoDeleteEnabled ->
                when {
                    autoDeleteEnabled -> pair
                    else -> pair.first to pair.second.map { it.copy(skipAutoDelete = null) }
                }
            }
            .onEach {
                setState {

                    val selectionState = selectionState.copy(
                        inMultiSelectMode = it.first,
                        selection = it.second,
                    )

                    copy(selectionState = selectionState)
                }
            }
            .launchIn(viewModelHandle.coroutineScope)
    }

    private fun buildSelectedRecording(
        recording: Recording,
    ): SelectedRecording = SelectedRecording(
        id = recording.id,
        isStarred = recording.is_starred,
        skipAutoDelete = recording.skip_auto_delete,
    )
}
