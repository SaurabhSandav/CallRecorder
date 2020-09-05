package com.redridgeapps.ui.main.handlers

import com.redridgeapps.callrecorder.callutils.Defaults
import com.redridgeapps.callrecorder.callutils.db.Recording
import com.redridgeapps.callrecorder.callutils.db.RecordingId
import com.redridgeapps.callrecorder.callutils.storage.Recordings
import com.redridgeapps.callrecorder.common.ViewModelHandle
import com.redridgeapps.callrecorder.common.utils.launchUnit
import com.redridgeapps.callrecorder.prefs.PREF_AUTO_DELETE_ENABLED
import com.redridgeapps.callrecorder.prefs.Prefs
import com.redridgeapps.ui.common.utils.ClickSelection
import com.redridgeapps.ui.main.SelectedRecording
import com.redridgeapps.ui.main.SetState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

internal class SelectionHandler constructor(
    private val viewModelHandle: ViewModelHandle,
    private val setState: SetState,
    private val recordingSelection: ClickSelection<SelectedRecording>,
    private val prefs: Prefs,
    private val recordings: Recordings,
) {

    init {
        viewModelHandle.onInit {
            observeSelection()
            observeAutoDeleteEnabled()
        }
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

        recordingSelection.state
            .flatMapLatest { state ->
                recordings.getRecordings(state.selection.map { it.id })
                    .map { list ->
                        state.inMultiSelectMode to list.map(this::buildSelectedRecording)
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

    private fun observeAutoDeleteEnabled() {

        prefs.boolean(PREF_AUTO_DELETE_ENABLED) { Defaults.AUTO_DELETE_ENABLED }
            .onEach { setState { copy(autoDeleteEnabled = it) } }
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
