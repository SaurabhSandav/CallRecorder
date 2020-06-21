package com.redridgeapps.callrecorder.ui.prefcomponents

import androidx.compose.Composable
import androidx.compose.state
import androidx.ui.animation.Crossfade
import androidx.ui.core.Alignment
import androidx.ui.core.ConfigurationAmbient
import androidx.ui.core.Modifier
import androidx.ui.foundation.*
import androidx.ui.foundation.selection.selectable
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.material.Divider
import androidx.ui.material.ListItem
import androidx.ui.material.MaterialTheme
import androidx.ui.material.RadioButton
import androidx.ui.text.TextStyle
import androidx.ui.unit.dp

@Composable
fun <T> SingleSelectListPreference(
    title: String,
    keys: List<T>,
    itemText: (key: T) -> String,
    selectedItem: T? = null,
    onSelectedChange: (key: T) -> Unit
) {

    val (showDialog, setShowDialog) = state { false }
    val secondaryText = if (selectedItem == null) "" else itemText(selectedItem)

    ListItem(
        text = { Text(title) },
        secondaryText = {
            Crossfade(current = secondaryText) {
                Text(it)
            }
        },
        onClick = { setShowDialog(true) }
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
    mainContent: @Composable() () -> Unit
) {
    Dialog(onCloseRequest = onCloseRequest) {
        Column(Modifier.drawBackground(Color.White).width(280.dp)) {

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

            VerticalScroller(
                modifier = Modifier.preferredHeightIn(maxHeight = mainContentHeight.dp),
                children = { mainContent() }
            )
        }
    }
}
