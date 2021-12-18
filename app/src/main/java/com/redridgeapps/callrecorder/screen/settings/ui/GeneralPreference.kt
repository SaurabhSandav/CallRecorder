package com.redridgeapps.callrecorder.screen.settings.ui

import androidx.compose.runtime.Composable
import com.redridgeapps.callrecorder.screen.common.pref.ButtonPreference
import com.redridgeapps.callrecorder.screen.common.pref.PreferenceCategory
import com.redridgeapps.callrecorder.screen.common.pref.SwitchPreference
import com.redridgeapps.callrecorder.screen.settings.Preference
import com.redridgeapps.callrecorder.screen.settings.collectPrefValue

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
