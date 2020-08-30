package com.redridgeapps.ui.settings.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.redridgeapps.ui.common.prefcomponents.SwitchPreference
import com.redridgeapps.ui.common.prefcomponents.TextFieldPreference
import com.redridgeapps.ui.common.prefcomponents.TitlePreference
import com.redridgeapps.ui.settings.PreferenceValue

@Composable
internal fun AutoDeletePreference(
    autoDeleteEnabled: PreferenceValue<Boolean>,
    autoDeleteAfterDays: PreferenceValue<Int>,
) {

    Column {

        TitlePreference(text = "Auto delete")

        SwitchPreference(
            text = "Auto delete",
            checked = autoDeleteEnabled.value,
            onCheckedChange = autoDeleteEnabled.onChanged
        )

        AnimatedVisibility(visible = autoDeleteEnabled.value) {

            TextFieldPreference(
                title = "Auto delete after",
                text = autoDeleteAfterDays.value.toString(),
                onValueChange = { autoDeleteAfterDays.onChanged(it.toInt()) }
            )
        }
    }
}
