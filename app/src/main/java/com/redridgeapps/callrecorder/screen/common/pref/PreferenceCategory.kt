package com.redridgeapps.callrecorder.screen.common.pref

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun PreferenceCategory(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {

    Column(modifier = modifier) {

        TitlePreference(text = title)

        content()
    }
}
