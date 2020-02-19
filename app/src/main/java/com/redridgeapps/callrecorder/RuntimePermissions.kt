package com.redridgeapps.callrecorder

import android.app.Activity
import androidx.compose.staticAmbientOf
import com.afollestad.assent.*
import com.afollestad.assent.rationale.RationaleHandler

class RuntimePermissions(private val activity: Activity) {

    fun askForPermissions(
        vararg permissions: Permission,
        requestCode: Int = 20,
        rationaleHandler: RationaleHandler? = null,
        callback: Callback
    ) {
        activity.askForPermissions(
            permissions = *permissions,
            requestCode = requestCode,
            rationaleHandler = rationaleHandler,
            callback = callback
        )
    }

    fun runWithPermissions(
        vararg permissions: Permission,
        requestCode: Int = 40,
        rationaleHandler: RationaleHandler? = null,
        execute: Callback
    ) {
        activity.runWithPermissions(
            permissions = *permissions,
            requestCode = requestCode,
            rationaleHandler = rationaleHandler,
            execute = execute
        )
    }

    fun showSystemAppDetailsPage() {
        activity.showSystemAppDetailsPage()
    }
}

val PermissionsAmbient = staticAmbientOf<RuntimePermissions>()
