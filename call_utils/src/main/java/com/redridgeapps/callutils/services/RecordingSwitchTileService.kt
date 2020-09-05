package com.redridgeapps.callutils.services

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.redridgeapps.callutils.Defaults
import com.redridgeapps.prefs.PREF_RECORDING_ENABLED
import com.redridgeapps.prefs.Prefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RecordingSwitchTileService : TileService() {

    @Inject
    lateinit var prefs: Prefs

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

        val flipped = !prefs
            .boolean(PREF_RECORDING_ENABLED) { Defaults.RECORDING_ENABLED }
            .first()

        prefs.editor { set(PREF_RECORDING_ENABLED, flipped) }

        updateTile()
    }

    private fun updateTile() = coroutineScope.launch {

        val recordingEnabled = prefs.boolean(PREF_RECORDING_ENABLED) {
            Defaults.RECORDING_ENABLED
        }.first()

        qsTile.state = if (recordingEnabled) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        qsTile.updateTile()
    }
}
