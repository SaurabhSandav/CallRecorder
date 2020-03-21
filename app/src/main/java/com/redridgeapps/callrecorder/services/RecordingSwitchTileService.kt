package com.redridgeapps.callrecorder.services

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.redridgeapps.callrecorder.utils.prefs.PREF_IS_RECORDING_ON
import com.redridgeapps.callrecorder.utils.prefs.Prefs
import dagger.android.AndroidInjection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

class RecordingSwitchTileService : TileService() {

    @Inject
    lateinit var prefs: Prefs

    private val coroutineScope = CoroutineScope(SupervisorJob())

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

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

        prefs.set(PREF_IS_RECORDING_ON, !prefs.get(PREF_IS_RECORDING_ON))

        updateTile()
    }

    private fun updateTile() = coroutineScope.launch {

        val isRecordingOn = prefs.get(PREF_IS_RECORDING_ON)

        qsTile.state = if (isRecordingOn) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        qsTile.updateTile()
    }
}
