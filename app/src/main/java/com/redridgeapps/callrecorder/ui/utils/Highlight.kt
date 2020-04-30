package com.redridgeapps.callrecorder.ui.utils

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.drawBackground
import androidx.ui.graphics.Color
import androidx.ui.material.MaterialTheme

@Composable
fun Highlight(enabled: Boolean, content: @Composable() () -> Unit) {

    if (enabled)
        Box(Modifier.drawScrim(), children = content)
    else
        content()
}

@Composable
private fun Modifier.drawScrim(color: Color = MaterialTheme.colors.onSurface): Modifier {
    return drawBackground(color.copy(alpha = SCRIM_ALPHA))
}

private const val SCRIM_ALPHA = 0.32F
