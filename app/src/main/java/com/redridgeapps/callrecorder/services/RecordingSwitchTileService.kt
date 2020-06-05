package com.redridgeapps.callrecorder.services

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.redridgeapps.callrecorder.utils.Defaults
import com.redridgeapps.callrecorder.utils.prefs.MyPrefs
import com.redridgeapps.callrecorder.utils.prefs.Prefs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
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

        val flippedIsRecordingOn = !prefs.get(MyPrefs.IS_RECORDING_ON) { Defaults.IS_RECORDING_ON }
        prefs.set(MyPrefs.IS_RECORDING_ON, flippedIsRecordingOn)

        updateTile()
    }

    private fun updateTile() = coroutineScope.launch {

        val isRecordingOn = prefs.get(MyPrefs.IS_RECORDING_ON) { Defaults.IS_RECORDING_ON }

        qsTile.state = if (isRecordingOn) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        qsTile.updateTile()
    }
}
