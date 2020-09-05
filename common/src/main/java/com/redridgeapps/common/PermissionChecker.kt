package com.redridgeapps.common

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PermissionChecker @Inject constructor(@ApplicationContext private val context: Context) {

    fun isPermissionGranted(permission: String): Boolean {
        val permissionResult = ContextCompat.checkSelfPermission(context, permission)
        return permissionResult == PackageManager.PERMISSION_GRANTED
    }
}
