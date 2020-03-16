package com.redridgeapps.ui.utils

import androidx.compose.Composable
import androidx.ui.core.Text
import androidx.ui.layout.LayoutPadding
import androidx.ui.material.*
import androidx.ui.text.TextStyle
import androidx.ui.text.font.FontWeight
import androidx.ui.unit.dp

@Composable
fun TitlePreference(text: String) {

    Text(
        text,
        LayoutPadding(top = 20.dp, start = 10.dp),
        TextStyle(color = MaterialTheme.colors().secondary, fontWeight = FontWeight.Bold)
    )
}

@Composable
fun CheckboxPreference(
    text: String,
    checked: Boolean? = null,
    onCheckedChange: (Boolean) -> Unit
) {

    ListItem(
        text = { Text(text) },
        trailing = {
            if (checked != null)
                Checkbox(checked = checked, onCheckedChange = onCheckedChange)
            else
                CircularProgressIndicator()
        }
    )
}

@Composable
fun SwitchPreference(
    text: String,
    checked: Boolean? = null,
    onCheckedChange: (Boolean) -> Unit
) {

    ListItem(
        text = { Text(text) },
        trailing = {
            if (checked != null)
                Switch(checked = checked, onCheckedChange = onCheckedChange)
            else
                CircularProgressIndicator()
        }
    )
}
