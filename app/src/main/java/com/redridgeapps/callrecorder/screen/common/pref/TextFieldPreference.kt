package com.redridgeapps.callrecorder.screen.common.pref

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ListItem
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun TextFieldPreference(
    title: String,
    text: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {

    var showDialog by remember { mutableStateOf(false) }

    ListItem(
        modifier = modifier.clickable(onClick = { showDialog = true }),
        secondaryText = {
            Crossfade(current = text) {
                Text(it)
            }
        },
        text = { Text(title) },
    )

    if (showDialog) {

        TextFieldPreferenceDialog(
            title = title,
            text = text,
            onValueChange = onValueChange,
            onDismiss = { showDialog = false }
        )
    }
}

@Composable
private fun TextFieldPreferenceDialog(
    title: String,
    text: String,
    onValueChange: (String) -> Unit,
    onDismiss: () -> Unit,
) {

    var textFieldValue by savedInstanceState(saver = TextFieldValue.Saver) {
        TextFieldValue(text = text, selection = TextRange(text.length))
    }

    PreferenceDialog(
        title = title,
        onDismiss = onDismiss,
        buttonContent = {
            DialogButtonContent(
                onCancel = onDismiss,
                onOk = {
                    onValueChange(textFieldValue.text)
                    onDismiss()
                }
            )
        },
        content = {
            DialogContent(
                textFieldValue = textFieldValue,
                onTextFieldValueChange = { textFieldValue = it }
            )
        }
    )
}

@Composable
private fun DialogContent(
    textFieldValue: TextFieldValue,
    onTextFieldValueChange: (TextFieldValue) -> Unit,
) {

    TextField(
        modifier = Modifier.padding(8.dp),
        value = textFieldValue,
        onValueChange = onTextFieldValueChange,
        keyboardType = KeyboardType.Number
    )
}

@Composable
private fun DialogButtonContent(
    onCancel: () -> Unit,
    onOk: () -> Unit,
) {

    Row(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        horizontalArrangement = Arrangement.End
    ) {

        TextButton(
            onClick = onCancel,
            content = { Text("CANCEL") }
        )

        TextButton(
            onClick = onOk,
            content = { Text("OK") }
        )
    }
}
