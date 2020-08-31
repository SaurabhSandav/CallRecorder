package com.redridgeapps.ui.main.ui

import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.material.Divider
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.redridgeapps.ui.main.RecordingListEntry
import com.redridgeapps.ui.main.RecordingListEntry.Header
import com.redridgeapps.ui.main.RecordingListEntry.Item

@Composable
internal fun RecordingList(
    recordingList: List<RecordingListEntry>,
    modifier: Modifier = Modifier,
) {

    LazyColumnFor(
        items = recordingList,
        modifier = modifier
    ) { entry ->

        when (entry) {
            is Header -> RecordingListHeader(header = entry.title)
            is Item -> RecordingListItem(item = entry)
        }
    }
}

@Composable
private fun RecordingListHeader(header: String) {

    Column {

        RecordingListDivider()

        Text(
            text = header,
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.fillMaxWidth()
                .padding(5.dp)
                .wrapContentWidth(Alignment.CenterHorizontally),
        )

        RecordingListDivider()
    }
}

@Composable
private fun RecordingListDivider() {

    Divider(
        modifier = Modifier.padding(start = 10.dp, end = 10.dp),
        color = MaterialTheme.colors.onSurface.copy(alpha = DIVIDER_ALPHA)
    )
}

@Composable
private fun RecordingListItem(item: Item) {

    var modifier = Modifier.clickable(
        onClick = item.onSelect,
        onLongClick = item.onMultiSelect
    )

    if (item.isSelected) {
        modifier = modifier.background(
            color = MaterialTheme.colors.onSurface.copy(alpha = SCRIM_ALPHA)
        )
    }

    if (item.isStarted) {
        modifier = modifier.background(
            color = MaterialTheme.colors.secondary.copy(alpha = SCRIM_ALPHA)
        )
    }

    ListItem(
        modifier = modifier,
        icon = {
            PlayPauseToggleButton(
                isPlaying = item.isPlaying,
                onClick = item.onPlayPauseToggle,
                iconSideSize = 45.dp
            )
        },
        secondaryText = { Text(item.number) },
        overlineText = { Text(item.overlineText) },
        trailing = { Text(item.metaText) },
        text = { Text(item.name) }
    )
}

private const val DIVIDER_ALPHA = 0.12F
private const val SCRIM_ALPHA = 0.32F
