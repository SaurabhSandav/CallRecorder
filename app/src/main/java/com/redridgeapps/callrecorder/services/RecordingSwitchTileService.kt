package com.redridgeapps.callrecorder.services

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.redridgeapps.callrecorder.prefs.PREF_IS_RECORDING_ON
import com.redridgeapps.callrecorder.prefs.Prefs
import com.redridgeapps.callrecorder.utils.constants.Defaults
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

        val flippedIsRecordingOn =
            !prefs.prefBoolean(PREF_IS_RECORDING_ON) { Defaults.IS_RECORDING_ON }.first()
        prefs.editor { setBoolean(PREF_IS_RECORDING_ON, flippedIsRecordingOn) }

        updateTile()
    }

    private fun updateTile() = coroutineScope.launch {

        val isRecordingOn =
            prefs.prefBoolean(PREF_IS_RECORDING_ON) { Defaults.IS_RECORDING_ON }.first()

        qsTile.state = if (isRecordingOn) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        qsTile.updateTile()
    }
}
