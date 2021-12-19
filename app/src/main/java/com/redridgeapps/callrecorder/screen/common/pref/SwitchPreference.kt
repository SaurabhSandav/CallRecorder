package com.redridgeapps.callrecorder.screen.common.pref

import androidx.compose.material.ListItem
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SwitchPreference(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {

    ListItem(
        text = { Text(text) },
        trailing = { Switch(checked = checked, onCheckedChange = onCheckedChange) },
        modifier = modifier,
    )
}
