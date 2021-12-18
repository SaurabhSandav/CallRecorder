package com.redridgeapps.callrecorder.screen.firstrun.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.redridgeapps.callrecorder.screen.firstrun.OnAppSystemize

@Composable
internal fun SystemizationConfig(
    isAppSystemized: Boolean,
    onAppSystemize: OnAppSystemize,
) {

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

        Text(text = "Systemization", style = MaterialTheme.typography.h6)

        Text(
            text = "App needs to be a system app. High quality call recording only works with system apps.",
            style = MaterialTheme.typography.subtitle1
        )

        var isAppSystemizedNullable: Boolean? by remember(isAppSystemized) {
            mutableStateOf(isAppSystemized)
        }

        Crossfade(current = isAppSystemizedNullable) { isAppSystemizedNullableCF ->

            when (isAppSystemizedNullableCF) {
                null -> CircularProgressIndicator()
                true -> Text(text = "✔ App is systemized")
                false -> Button(
                    onClick = {
                        onAppSystemize()
                        isAppSystemizedNullable = null
                    },
                    content = { Text(text = "Systemize App") },
                )
            }
        }
    }
}
