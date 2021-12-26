package com.redridgeapps.callutils.services

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.redridgeapps.common.PrefKeys
import com.russhwolf.settings.coroutines.FlowSettings
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RecordingSwitchTileService : TileService() {

    @Inject
    lateinit var prefs: FlowSettings

    private val coroutineScope = CoroutineScope(SupervisorJob())

    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
    }

    override fun onTileAdded() {
        super.onTileAdded()
        updateTile()
    }

    override fun onStartListening() {
        super.onStartListening()
        updateTile()
    }

    override fun onClick() {
        super.onClick()
        flipTile()
    }

    private fun flipTile() = coroutineScope.launch {

        val flipped = prefs.getBoolean(PrefKeys.isRecordingEnabled).not()

        prefs.putBoolean(PrefKeys.isRecordingEnabled, flipped)

        updateTile()
    }

    private fun updateTile() = coroutineScope.launch {

        val recordingEnabled = prefs.getBoolean(PrefKeys.isRecordingEnabled)

        qsTile.state = if (recordingEnabled) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        qsTile.updateTile()
    }
}
