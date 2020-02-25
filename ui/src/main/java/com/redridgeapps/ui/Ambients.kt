package com.redridgeapps.ui

import androidx.compose.Composable
import androidx.compose.ProvidedValue
import androidx.compose.Providers
import androidx.compose.staticAmbientOf
import com.redridgeapps.repository.ICallPlayback
import com.redridgeapps.repository.ICallRecorder

val CallRecorderAmbient = staticAmbientOf<ICallRecorder>()
val CallPlaybackAmbient = staticAmbientOf<ICallPlayback>()

@Composable
fun WithAmbients(vararg values: ProvidedValue<*>, content: @Composable() () -> Unit) {
    Providers(*values, children = content)
}
