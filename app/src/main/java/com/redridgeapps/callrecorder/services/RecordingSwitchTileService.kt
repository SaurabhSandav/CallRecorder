package com.redridgeapps.callrecorder.services

import android.content.SharedPreferences
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.redridgeapps.callrecorder.utils.PREF_IS_RECORDING_ON
import com.redridgeapps.callrecorder.utils.get
import com.redridgeapps.callrecorder.utils.modify
import dagger.android.AndroidInjection
import javax.inject.Inject

class RecordingSwitchTileService : TileService() {

    @Inject
    lateinit var prefs: SharedPreferences

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

        prefs.modify(PREF_IS_RECORDING_ON, isRecordingOn)

        qsTile.state = if (!isRecordingOn) Tile.STATE_INACTIVE else {
            CallingService.startSurveillance(applicationContext)
            Tile.STATE_ACTIVE
        }

        qsTile.updateTile()
    }
}
