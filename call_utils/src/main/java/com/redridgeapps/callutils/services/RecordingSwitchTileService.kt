package com.redridgeapps.callutils.services

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.datastore.core.DataStore
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
    lateinit var prefs: DataStore<Prefs>

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

        val flipped = prefs.data.first().is_recording_enabled.not()

        prefs.updateData { it.copy(is_recording_enabled = flipped) }

        updateTile()
    }

    private fun updateTile() = coroutineScope.launch {

        val recordingEnabled = prefs.data.first().is_recording_enabled

        qsTile.state = if (recordingEnabled) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        qsTile.updateTile()
    }
}
