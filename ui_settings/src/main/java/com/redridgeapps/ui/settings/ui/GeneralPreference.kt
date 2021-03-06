package com.redridgeapps.ui.settings.ui

import androidx.compose.runtime.Composable
import com.redridgeapps.ui.common.pref.ButtonPreference
import com.redridgeapps.ui.common.pref.PreferenceCategory
import com.redridgeapps.ui.common.pref.SwitchPreference
import com.redridgeapps.ui.settings.PreferenceValue

@Composable
internal fun GeneralPreference(
    isAppSystemized: PreferenceValue<Boolean>,
    recordingEnabled: PreferenceValue<Boolean>,

    onUpdateContactNames: () -> Unit,
) {

    PreferenceCategory(title = "Auto delete") {

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

        ButtonPreference(
            text = "Update contact names",
            onClick = onUpdateContactNames,
        )
    }
}
