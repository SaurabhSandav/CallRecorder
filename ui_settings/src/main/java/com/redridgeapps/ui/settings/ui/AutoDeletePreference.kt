package com.redridgeapps.ui.settings.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import com.redridgeapps.ui.common.pref.PreferenceCategory
import com.redridgeapps.ui.common.pref.SwitchPreference
import com.redridgeapps.ui.common.pref.TextFieldPreference
import com.redridgeapps.ui.settings.Preference
import com.redridgeapps.ui.settings.collectPrefValue

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
