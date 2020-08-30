package com.redridgeapps.ui.common.pref

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

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
    checked: Boolean?,
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
