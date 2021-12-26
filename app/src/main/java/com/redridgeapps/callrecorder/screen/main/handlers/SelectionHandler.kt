package com.redridgeapps.callrecorder.screen.main.handlers

import com.redridgeapps.callrecorder.screen.common.utils.ClickSelection
import com.redridgeapps.callrecorder.screen.main.SelectedRecording
import com.redridgeapps.callrecorder.screen.main.SetState
import com.redridgeapps.callutils.db.Recording
import com.redridgeapps.callutils.db.RecordingId
import com.redridgeapps.callutils.storage.Recordings
import com.redridgeapps.common.PrefKeys
import com.redridgeapps.common.utils.launchUnit
import com.redridgeapps.common.viewmodel.ViewModelHandle
import com.russhwolf.settings.coroutines.FlowSettings
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
    private val prefs: FlowSettings,
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

        val autoDeleteEnabledFlow = prefs.getBooleanFlow(PrefKeys.isAutoDeleteEnabled)

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
