package com.redridgeapps.callrecorder.ui.prefcomponents

import androidx.compose.Composable
import androidx.compose.getValue
import androidx.compose.setValue
import androidx.compose.state
import androidx.ui.animation.Crossfade
import androidx.ui.core.Modifier
import androidx.ui.foundation.*
import androidx.ui.graphics.Color
import androidx.ui.input.KeyboardType
import androidx.ui.input.TextFieldValue
import androidx.ui.layout.*
import androidx.ui.material.ListItem
import androidx.ui.material.MaterialTheme
import androidx.ui.material.TextButton
import androidx.ui.savedinstancestate.savedInstanceState
import androidx.ui.text.TextRange
import androidx.ui.unit.dp

@Composable
fun TextFieldPreference(
    title: String,
    text: String,
    onValueChange: (String) -> Unit
) {

    val (showDialog, setShowDialog) = state { false }

    ListItem(
        text = { Text(title) },
        secondaryText = {
            Crossfade(current = text) {
                Text(it)
            }
        },
        onClick = { setShowDialog(true) }
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

            // Main content

            var textFieldValue by savedInstanceState(saver = TextFieldValue.Saver) {
                TextFieldValue(text = text, selection = TextRange(text.length))
            }

            TextField(
                modifier = Modifier.padding(8.dp),
                value = textFieldValue,
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
