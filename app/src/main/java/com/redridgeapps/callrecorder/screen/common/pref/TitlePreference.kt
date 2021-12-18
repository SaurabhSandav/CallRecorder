package com.redridgeapps.callrecorder.screen.common.pref

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TitlePreference(
    text: String,
    modifier: Modifier = Modifier,
) {

    Text(
        text = text,
        modifier = modifier.padding(start = 10.dp, top = 20.dp),
        style = TextStyle(color = MaterialTheme.colors.secondary, fontWeight = FontWeight.Bold)
    )
}
