package com.pittvandewitt.nightmodetile

import android.app.UiModeManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.IBinder
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class ToggleService : TileService() {

    private var updateAfterBind: ToggleService? = null

    override fun onBind(intent: Intent?): IBinder? {
        updateAfterBind = this
        requestListeningState(this, ComponentName(this, ToggleService::class.java))
        return super.onBind(intent)
    }

    /**
     * Called when this tile moves into a listening state.
     *
     *
     * When this tile is in a listening state it is expected to keep the
     * UI up to date.  Any listeners or callbacks needed to keep this tile
     * up to date should be registered here and unregistered in [.onStopListening].
     *
     * @see .getQsTile
     * @see Tile.updateTile
     */
    override fun onStartListening() {
        super.onStartListening()
        // Prevent updating tile before NIGHT_MODE has changed unless onBind is called
        if (updateAfterBind != null) {
            updateTile(this)
            updateAfterBind = null
        }
    }

    /**
     * Called when the user clicks on this tile.
     */
    override fun onClick() {
        super.onClick()
        with(getSystemService(Context.UI_MODE_SERVICE) as UiModeManager) {
            when (nightMode) {
                UiModeManager.MODE_NIGHT_AUTO -> nightMode = UiModeManager.MODE_NIGHT_NO
                UiModeManager.MODE_NIGHT_NO -> nightMode = UiModeManager.MODE_NIGHT_YES
                UiModeManager.MODE_NIGHT_YES -> nightMode = UiModeManager.MODE_NIGHT_AUTO
            }
        }
        updateTile(this)
    }

    private fun updateTile(context: Context) {
        when ((getSystemService(Context.UI_MODE_SERVICE) as UiModeManager).nightMode) {
            UiModeManager.MODE_NIGHT_AUTO -> qsTile?.let {
                it.icon = Icon.createWithResource(context, R.drawable.ic_night_mode_auto)
                it.label = getString(R.string.auto)
                it.state = Tile.STATE_ACTIVE
                it.updateTile()
            }
            UiModeManager.MODE_NIGHT_NO -> qsTile?.let {
                it.icon = Icon.createWithResource(context, R.drawable.ic_night_mode_off)
                it.label = getString(R.string.off)
                it.state = Tile.STATE_INACTIVE
                it.updateTile()
            }
            UiModeManager.MODE_NIGHT_YES -> qsTile?.let {
                it.icon = Icon.createWithResource(context, R.drawable.ic_night_mode_on)
                it.label = getString(R.string.on)
                it.state = Tile.STATE_ACTIVE
                it.updateTile()
            }
        }
    }
}