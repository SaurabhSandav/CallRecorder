package com.redridgeapps.ui

import androidx.compose.Composable
import androidx.ui.material.MaterialTheme
import com.redridgeapps.repository.ISystemizer
import javax.inject.Inject

class SplashUIInitializer @Inject constructor(
    private val systemizer: ISystemizer
) : UIInitializer {

    @Composable
    override fun initialize() {
        SplashUI(systemizer)
    }
}

@Composable
fun SplashUI(systemizer: ISystemizer) {
    MaterialTheme {

        val initializer = if (systemizer.isAppSystemized()) MainUIInitializer::class
        else SystemizerUIInitializer::class

        UIInitializer.get(initializer).initialize()
    }
}
