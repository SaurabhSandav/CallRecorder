package com.redridgeapps.callrecorder.screen.main.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.ListItem
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.annotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.redridgeapps.callrecorder.screen.common.generic.MaterialDialog
import com.redridgeapps.callrecorder.screen.common.pref.ButtonPreference
import com.redridgeapps.callrecorder.screen.common.pref.SwitchPreference
import com.redridgeapps.callrecorder.screen.main.OptionsDialogTab
import com.redridgeapps.callrecorder.screen.main.SelectedRecording
import com.redridgeapps.callrecorder.screen.main.SelectedRecordingOperations

@Composable
internal fun OptionsDialog(
    selectedRecording: SelectedRecording,
    selectedRecordingOperations: SelectedRecordingOperations,
    onDismissRequest: () -> Unit,
) {

    MaterialDialog(onDismissRequest = onDismissRequest) {

        val tabs = remember { OptionsDialogTab.values().asList() }
        var selectedTab by remember { mutableStateOf(tabs.first()) }

        TabRow(selectedTabIndex = selectedTab.ordinal) {

            for (tab in tabs) {

                Tab(
                    selected = selectedTab.ordinal == tab.ordinal,
                    onClick = { selectedTab = tab },
                    text = { Text(tab.toReadableString()) }
                )
            }
        }

        val recordingInfo by annotatedRecordingInfo(
            getInfoMap = selectedRecordingOperations.getInfoMap
        )

        Crossfade(selectedTab, Modifier.animateContentSize()) { tabIndex ->

            ScrollableColumn {

                when (tabIndex) {
                    OptionsDialogTab.OPTIONS -> OptionsDialogOptionsTab(
                        selectedRecording = selectedRecording,
                        selectedRecordingOperations = selectedRecordingOperations,
                    )
                    OptionsDialogTab.INFO -> OptionsDialogInfoTab(recordingInfo)
                }
            }
        }
    }
}

private fun OptionsDialogTab.toReadableString(): String = when (this) {
    OptionsDialogTab.OPTIONS -> "Options"
    OptionsDialogTab.INFO -> "Info"
}

@Composable
private fun annotatedRecordingInfo(
    getInfoMap: suspend () -> Map<String, String>,
): State<List<AnnotatedString>> = produceState(initialValue = emptyList(), getInfoMap) {
    value = getInfoMap().map {
        annotatedString {
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(it.key) }
            append(it.value)
        }
    }
}

@Composable
private fun ColumnScope.OptionsDialogOptionsTab(
    selectedRecording: SelectedRecording,
    selectedRecordingOperations: SelectedRecordingOperations,
) {

    SwitchPreference(
        text = "Star",
        checked = selectedRecording.isStarred,
        onCheckedChange = selectedRecordingOperations.onToggleStar,
    )

    AnimatedVisibility(visible = selectedRecording.skipAutoDelete != null) {

        SwitchPreference(
            text = "Skip auto delete",
            checked = selectedRecording.skipAutoDelete ?: false,
            onCheckedChange = selectedRecordingOperations.onToggleSkipAutoDelete,
        )
    }

    ButtonPreference(
        text = "Trim silence at start/end",
        onClick = selectedRecordingOperations.onTrimSilenceEnds
    )

    ButtonPreference(
        text = "Convert to Mp3",
        onClick = selectedRecordingOperations.onConvertToMp3
    )

    ButtonPreference(
        text = "Delete",
        onClick = selectedRecordingOperations.onDeleteRecordings
    )
}

@Composable
private fun OptionsDialogInfoTab(recordingInfo: List<AnnotatedString>) {

    for (entry in recordingInfo) {
        ListItem(text = { Text(entry) })
    }
}
