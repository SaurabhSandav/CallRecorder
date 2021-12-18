package com.redridgeapps.callrecorder.screen.common.pref

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeightIn
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Divider
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ConfigurationAmbient
import androidx.compose.ui.unit.dp

@Composable
fun <T> SingleSelectListPreference(
    title: String,
    keys: List<T>,
    itemLabel: (key: T) -> String,
    selectedItem: T,
    onSelectedChange: (key: T) -> Unit,
) {

    var showDialog by remember { mutableStateOf(false) }

    ListItem(
        modifier = Modifier.clickable(onClick = { showDialog = true }),
        secondaryText = {
            Crossfade(current = itemLabel(selectedItem)) {
                Text(it)
            }
        },
        text = { Text(title) },
    )

    if (showDialog) {

        SingleSelectListPreferenceDialog(
            title = title,
            keys = keys,
            itemLabel = itemLabel,
            selectedItem = selectedItem,
            onSelectedChange = onSelectedChange,
            onDismiss = { showDialog = false },
        )
    }
}

@Composable
private fun <T> SingleSelectListPreferenceDialog(
    title: String,
    keys: List<T>,
    itemLabel: (key: T) -> String,
    selectedItem: T,
    onSelectedChange: (key: T) -> Unit,
    onDismiss: () -> Unit,
) {

    PreferenceDialog(
        title = title,
        onDismiss = onDismiss
    ) {

        DialogContent(
            onDismiss = onDismiss,
            keys = keys,
            itemLabel = itemLabel,
            selectedItem = selectedItem,
            onSelectedChange = onSelectedChange,
        )
    }
}

@Composable
private fun <T> DialogContent(
    keys: List<T>,
    itemLabel: (T) -> String,
    selectedItem: T,
    onSelectedChange: (T) -> Unit,
    onDismiss: () -> Unit,
) {

    Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.12F))

    LazyColumnFor(
        items = keys,
        modifier = Modifier.preferredHeightIn(
            max = (ConfigurationAmbient.current.screenHeightDp - 150).dp
        ),
    ) { key ->

        RadioItem(
            label = itemLabel(key),
            isSelected = key == selectedItem,
            onSelect = {
                onSelectedChange(key)
                onDismiss()
            }
        )
    }
}

@Composable
private fun RadioItem(
    label: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
) {

    Row(
        modifier = Modifier.fillMaxWidth().height(48.dp).selectable(
            selected = isSelected,
            onClick = onSelect
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        RadioButton(
            selected = isSelected,
            onClick = onSelect,
            modifier = Modifier.padding(start = 24.dp),
        )

        Text(
            text = label,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}
