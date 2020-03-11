package com.redridgeapps.callrecorder.services

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.redridgeapps.callrecorder.utils.prefs.PREF_IS_RECORDING_ON
import com.redridgeapps.callrecorder.utils.prefs.Prefs
import dagger.android.AndroidInjection
import javax.inject.Inject

class RecordingSwitchTileService : TileService() {

    @Inject
    lateinit var prefs: Prefs

    private var isRecordingOn = false

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
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

    private fun updateTile() {

        isRecordingOn = prefs.get(PREF_IS_RECORDING_ON)

        qsTile.state = if (isRecordingOn) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        qsTile.updateTile()
    }

    private fun flipTile() {

        val prefIsRecordingOn = prefs.get(PREF_IS_RECORDING_ON)
        isRecordingOn = !prefIsRecordingOn

        prefs.set(PREF_IS_RECORDING_ON, isRecordingOn)

        qsTile.state = if (!isRecordingOn) Tile.STATE_INACTIVE else {
            CallingService.startSurveillance(applicationContext)
            Tile.STATE_ACTIVE
        }

        qsTile.updateTile()
    }
}
