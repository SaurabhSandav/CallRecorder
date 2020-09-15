package com.redridgeapps.ui.main

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.redridgeapps.callutils.playback.CallPlayback
import com.redridgeapps.callutils.services.AudioEndsTrimmingServiceLauncher
import com.redridgeapps.callutils.services.Mp3ConversionServiceLauncher
import com.redridgeapps.callutils.storage.Recordings
import com.redridgeapps.common.viewmodel.createViewModelHandle
import com.redridgeapps.prefs.Prefs
import com.redridgeapps.ui.common.utils.ClickSelection
import com.redridgeapps.ui.main.handlers.PlaybackHandler
import com.redridgeapps.ui.main.handlers.RecordingListHandler
import com.redridgeapps.ui.main.handlers.SelectedOperationsHandler
import com.redridgeapps.ui.main.handlers.SelectionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class MainViewModel @ViewModelInject constructor(
    @Assisted savedStateHandle: SavedStateHandle,
    prefs: Prefs,
    callPlayback: CallPlayback,
    recordings: Recordings,
    mp3ConversionServiceLauncher: Mp3ConversionServiceLauncher,
    audioEndsTrimmingServiceLauncher: AudioEndsTrimmingServiceLauncher,
) : ViewModel() {

    private val viewModelHandle = createViewModelHandle(savedStateHandle)
    private val recordingSelection = ClickSelection<SelectedRecording>()

    private val selectionHandler = SelectionHandler(
        viewModelHandle = viewModelHandle,
        setState = this::setState,
        recordingSelection = recordingSelection,
        prefs = prefs,
        recordings = recordings,
    )

    private val selectedOperationsHandler = SelectedOperationsHandler(
        viewModelHandle = viewModelHandle,
        recordingSelection = recordingSelection,
        recordings = recordings,
        mp3ConversionServiceLauncher = mp3ConversionServiceLauncher,
        audioEndsTrimmingServiceLauncher = audioEndsTrimmingServiceLauncher,
    )

    private val playbackHandler = PlaybackHandler(
        viewModelHandle = viewModelHandle,
        setState = this::setState,
        recordings = recordings,
        callPlayback = callPlayback,
    )

    private val recordingListHandler = RecordingListHandler(
        viewModelHandle = viewModelHandle,
        setState = this::setState,
        recordingSelection = recordingSelection,
        recordings = recordings,
        callPlayback = callPlayback,
        onRecordingSelect = selectionHandler::onSelect,
        onRecordingMultiSelect = selectionHandler::onMultiSelect,
        onRecordingPlayPauseToggle = playbackHandler::onPlayPauseToggle
    )

    private val initialState = MainState(
        filterState = FilterState(
            onToggleFilter = recordingListHandler::onToggleRecordingListFilter,
            onClearRecordingListFilters = recordingListHandler::onClearRecordingListFilters,
        ),
        selectionState = SelectionState(
            onCloseSelectionMode = selectionHandler::onCloseSelectionMode,
        ),
        selectedRecordingOperations = SelectedRecordingOperations(
            onDeleteRecordings = selectedOperationsHandler::onDeleteRecordings,
            onToggleStar = selectedOperationsHandler::onToggleStar,
            onToggleSkipAutoDelete = selectedOperationsHandler::onToggleSkipAutoDelete,
            onTrimSilenceEnds = selectedOperationsHandler::onTrimSilenceEnds,
            onConvertToMp3 = selectedOperationsHandler::onConvertToMp3,
            getInfoMap = selectedOperationsHandler::getInfoMap,
        )
    )

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<MainState> by ::_uiState

    init {
        viewModelHandle.init()
    }

    private fun setState(block: MainState.() -> MainState) {
        _uiState.value = _uiState.value.block()
    }
}
