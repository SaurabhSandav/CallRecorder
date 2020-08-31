package com.redridgeapps.ui.settings.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import com.redridgeapps.ui.common.pref.PreferenceCategory
import com.redridgeapps.ui.common.pref.SwitchPreference
import com.redridgeapps.ui.common.pref.TextFieldPreference
import com.redridgeapps.ui.settings.PreferenceValue

@Composable
internal fun AutoDeletePreference(
    autoDeleteEnabled: PreferenceValue<Boolean>,
    autoDeleteAfterDays: PreferenceValue<Int>,
) {

    PreferenceCategory(title = "Auto delete") {

        SwitchPreference(
            text = "Auto delete",
            checked = autoDeleteEnabled.value,
            onCheckedChange = autoDeleteEnabled.onChanged
        )

        AnimatedVisibility(visible = autoDeleteEnabled.value) {

            TextFieldPreference(
                title = "Auto delete after (days)",
                text = autoDeleteAfterDays.value.toString(),
                onValueChange = { autoDeleteAfterDays.onChanged(it.toInt()) }
            )
        }
    }
}
