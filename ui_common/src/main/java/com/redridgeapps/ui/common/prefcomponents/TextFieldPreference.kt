package com.redridgeapps.ui.common.prefcomponents

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Box
import androidx.compose.foundation.ContentGravity
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun TextFieldPreference(
    title: String,
    text: String,
    onValueChange: (String) -> Unit
) {

    val (showDialog, setShowDialog) = remember { mutableStateOf(false) }

    ListItem(
        modifier = Modifier.clickable(onClick = { setShowDialog(true) }),
        secondaryText = {
            Crossfade(current = text) {
                Text(it)
            }
        },
        text = { Text(title) },
    )

    if (showDialog) {
        DialogPreference(
            title = title,
            text = text,
            onCloseRequest = { setShowDialog(false) },
            onValueChange = {
                onValueChange(it)
                setShowDialog(false)
            }
        )
    }
}

@Composable
private fun DialogPreference(
    title: String,
    text: String,
    onCloseRequest: () -> Unit,
    onValueChange: (String) -> Unit
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

            // Main content

            var textFieldValue by savedInstanceState(saver = TextFieldValue.Saver) {
                TextFieldValue(text = text, selection = TextRange(text.length))
            }

            TextField(
                modifier = Modifier.padding(8.dp),
                value = textFieldValue,
                label = {},
                onValueChange = { textFieldValue = it },
                keyboardType = KeyboardType.Number
            )

            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.End
            ) {

                TextButton(onClick = onCloseRequest) {
                    Text("CANCEL")
                }

                TextButton(
                    onClick = { onValueChange(textFieldValue.text) }
                ) {
                    Text("OK")
                }
            }
        }
    }
}
