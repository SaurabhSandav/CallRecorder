package com.redridgeapps.callrecorder.screen.settings.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import com.redridgeapps.callrecorder.screen.common.pref.PreferenceCategory
import com.redridgeapps.callrecorder.screen.common.pref.SwitchPreference
import com.redridgeapps.callrecorder.screen.common.pref.TextFieldPreference
import com.redridgeapps.callrecorder.screen.settings.Preference
import com.redridgeapps.callrecorder.screen.settings.collectPrefValue

@Composable
internal fun AutoDeletePreference(
    autoDeleteEnabled: Preference<Boolean>,
    autoDeleteAfterDays: Preference<Int>,
) {

    PreferenceCategory(title = "Auto delete") {

        val autoDeleteEnabledValue = autoDeleteEnabled.collectPrefValue()

        SwitchPreference(
            text = "Auto delete",
            checked = autoDeleteEnabledValue,
            onCheckedChange = autoDeleteEnabled.onChanged
        )

        AnimatedVisibility(visible = autoDeleteEnabledValue) {

            TextFieldPreference(
                title = "Auto delete after (days)",
                text = autoDeleteAfterDays.collectPrefValue().toString(),
                onValueChange = { autoDeleteAfterDays.onChanged(it.toInt()) }
            )
        }
    }
}
