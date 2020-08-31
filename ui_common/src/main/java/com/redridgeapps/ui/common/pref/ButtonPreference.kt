package com.redridgeapps.ui.common.pref

import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.material.ListItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ButtonPreference(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {

    ListItem(
        text = { Text(text) },
        modifier = modifier.clickable(onClick = onClick),
    )
}
