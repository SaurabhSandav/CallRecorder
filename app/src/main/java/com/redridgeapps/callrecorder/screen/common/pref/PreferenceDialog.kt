package com.redridgeapps.callrecorder.screen.common.pref

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.redridgeapps.callrecorder.screen.common.generic.MaterialDialog

@Composable
internal fun PreferenceDialog(
    title: String,
    onDismiss: () -> Unit,
    buttonContent: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {

    MaterialDialog(onDismissRequest = onDismiss, modifier = Modifier.width(280.dp)) {

        // Title
        Text(
            text = title,
            modifier = Modifier.height(56.dp)
                .padding(horizontal = 24.dp)
                .wrapContentHeight(Alignment.CenterVertically),
            style = MaterialTheme.typography.h6
        )

        content()

        buttonContent?.let { it() }
    }
}
