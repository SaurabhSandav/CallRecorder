package com.redridgeapps.ui.settings.ui

import androidx.compose.foundation.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.ListItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.redridgeapps.ui.common.prefcomponents.SwitchPreference
import com.redridgeapps.ui.common.prefcomponents.TitlePreference
import com.redridgeapps.ui.settings.PreferenceValue

@Composable
internal fun GeneralPreference(
    isAppSystemized: PreferenceValue<Boolean>,
    recordingEnabled: PreferenceValue<Boolean>,

    onUpdateContactNames: () -> Unit,
) {

    Column {

        TitlePreference(text = "General")

        SwitchPreference(
            text = "Systemize",
            checked = isAppSystemized.value,
            onCheckedChange = isAppSystemized.onChanged
        )

        SwitchPreference(
            text = "Recording",
            checked = recordingEnabled.value,
            onCheckedChange = recordingEnabled.onChanged
        )

        ListItem(
            modifier = Modifier.clickable(onClick = onUpdateContactNames),
            text = { Text("Update contact names") },
        )
    }
}
