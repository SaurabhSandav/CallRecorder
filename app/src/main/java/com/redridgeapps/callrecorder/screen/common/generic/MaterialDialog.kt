package com.redridgeapps.callrecorder.screen.common.generic

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog

@Composable
fun MaterialDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    children: @Composable ColumnScope.() -> Unit,
) {

    Dialog(onDismissRequest = onDismissRequest) {
        Surface {
            Column(modifier = modifier, content = children)
        }
    }
}
