package com.redridgeapps.callrecorder.screen.main.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.redridgeapps.callrecorder.screen.main.RecordingListState

@Composable
internal fun ScaffoldBody(
    recordingListState: RecordingListState,
    modifier: Modifier,
) {

    Crossfade(recordingListState.isRecordingListRefreshing) { isRefreshing ->

        when {
            isRefreshing -> CircularProgressIndicator(
                modifier = modifier.fillMaxSize().wrapContentSize(Alignment.Center)
            )
            else -> RecordingList(
                recordingList = recordingListState.recordingList,
                modifier = modifier.fillMaxSize(),
            )
        }
    }
}
