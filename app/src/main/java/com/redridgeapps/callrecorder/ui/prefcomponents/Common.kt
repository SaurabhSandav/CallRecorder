package com.redridgeapps.callrecorder.ui.prefcomponents

import androidx.compose.Composable
import androidx.ui.animation.Crossfade
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.layout.padding
import androidx.ui.material.ListItem
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Switch
import androidx.ui.text.TextStyle
import androidx.ui.text.font.FontWeight
import androidx.ui.unit.dp

@Composable
fun TitlePreference(text: String) {

    Text(
        text = text,
        modifier = Modifier.padding(start = 10.dp, top = 20.dp),
        style = TextStyle(color = MaterialTheme.colors.secondary, fontWeight = FontWeight.Bold)
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
            Crossfade(current = checked) {
                if (it != null)
                    Switch(checked = it, onCheckedChange = onCheckedChange)
            }
        }
    )
}
