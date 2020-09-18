package com.redridgeapps.ui.settings.ui

import androidx.compose.runtime.Composable
import com.redridgeapps.ui.common.pref.ButtonPreference
import com.redridgeapps.ui.common.pref.PreferenceCategory
import com.redridgeapps.ui.common.pref.SwitchPreference
import com.redridgeapps.ui.settings.Preference
import com.redridgeapps.ui.settings.collectPrefValue

@Composable
internal fun GeneralPreference(
    isAppSystemized: Preference<Boolean>,
    recordingEnabled: Preference<Boolean>,

    onUpdateContactNames: () -> Unit,
) {

    PreferenceCategory(title = "Auto delete") {

        SwitchPreference(
            text = "Systemize",
            checked = isAppSystemized.collectPrefValue(),
            onCheckedChange = isAppSystemized.onChanged
        )

        SwitchPreference(
            text = "Recording",
            checked = recordingEnabled.collectPrefValue(),
            onCheckedChange = recordingEnabled.onChanged
        )

        ButtonPreference(
            text = "Update contact names",
            onClick = onUpdateContactNames,
        )
    }
}
