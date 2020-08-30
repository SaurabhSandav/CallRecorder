package com.redridgeapps.ui.common.prefcomponents

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Box
import androidx.compose.foundation.ContentGravity
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Divider
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.RadioButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ConfigurationAmbient
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun <T> SingleSelectListPreference(
    title: String,
    keys: List<T>,
    itemText: (key: T) -> String,
    selectedItem: T? = null,
    onSelectedChange: (key: T) -> Unit
) {

    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }
    val secondaryText = if (selectedItem == null) "" else itemText(selectedItem)

    ListItem(
        modifier = Modifier.clickable(onClick = { setShowDialog(true) }),
        secondaryText = {
            Crossfade(current = secondaryText) {
                Text(it)
            }
        },
        text = { Text(title) },
    )

    if (showDialog) {
        RadioGroupDialogPreference(
            title = title,
            setShowDialog = setShowDialog,
            keys = keys,
            itemText = itemText,
            selectedItem = selectedItem,
            onSelectedChange = onSelectedChange
        )
    }
}

@Composable
private fun <T> RadioGroupDialogPreference(
    title: String,
    setShowDialog: (Boolean) -> Unit,
    keys: List<T>,
    itemText: (key: T) -> String,
    selectedItem: T?,
    onSelectedChange: (key: T) -> Unit
) {

    DialogPreference(
        title = title,
        onCloseRequest = { setShowDialog(false) }
    ) {
        KeyedRadioGroup(
            keys = keys,
            itemText = itemText,
            selectedItem = selectedItem,
            onSelectedChange = {
                onSelectedChange(it)
                setShowDialog(false)
            }
        )
    }
}

@Composable
private fun <T> KeyedRadioGroup(
    keys: List<T>,
    itemText: (T) -> String,
    selectedItem: T?,
    onSelectedChange: (T) -> Unit,
    radioColor: Color = MaterialTheme.colors.secondary,
    textStyle: TextStyle? = null
) {

    Column {
        keys.forEach { key ->

            val selected = (key == selectedItem)
            val onSelect = { onSelectedChange(key) }

            Row(
                modifier = Modifier.fillMaxWidth().height(48.dp).selectable(
                    selected = selected,
                    onClick = onSelect
                ),
                verticalGravity = Alignment.CenterVertically
            ) {

                RadioButton(
                    selected = selected,
                    onClick = onSelect,
                    modifier = Modifier.padding(start = 24.dp),
                    selectedColor = radioColor
                )

                Text(
                    text = itemText(key),
                    style = MaterialTheme.typography.body1.merge(textStyle),
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    }
}

@Composable
private fun DialogPreference(
    title: String,
    onCloseRequest: () -> Unit,
    mainContent: @Composable () -> Unit
) {
    Dialog(onDismissRequest = onCloseRequest) {

        Column(Modifier.background(Color.White).width(280.dp)) {

            // Title

            Box(
                modifier = Modifier.height(56.dp).padding(horizontal = 24.dp),
                gravity = ContentGravity.CenterStart
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.h6.copy(MaterialTheme.colors.onSurface)
                )
            }

            // Divider

            Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.12F))

            // Main content

            val mainContentHeight = ConfigurationAmbient.current.screenHeightDp - 150

            ScrollableColumn(
                modifier = Modifier.preferredHeightIn(maxHeight = mainContentHeight.dp),
                children = { mainContent() }
            )
        }
    }
}
