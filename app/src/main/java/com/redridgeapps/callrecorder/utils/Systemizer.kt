package com.redridgeapps.callrecorder.utils

import android.content.Context
import android.content.pm.PackageManager
import com.redridgeapps.callrecorder.common.StartupInitializer
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.io.SuFile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class Systemizer @Inject constructor(
    @ApplicationContext context: Context
) {

    private val packageName = context.packageName
    private val currentApkLocation = context.applicationInfo.sourceDir
    private val permissions: List<String> = run {
        val info =
            context.packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
        info.requestedPermissions.asList()
    }
    private val tmpDir = context.cacheDir
    private val reCheckAppSystemizedChannel = BroadcastChannel<Unit>(CONFLATED)

    val isAppSystemizedFlow = reCheckAppSystemizedChannel.asFlow()
        .onStart { emit(Unit) }
        .map { isAppSystemized() }

    suspend fun systemize() = withContext(Dispatchers.IO) {
        if (isAppSystemized()) return@withContext

        withWritableSystem {
            installAPK(currentApkLocation)
            installPermissions(permissions, tmpDir)
        }

        reCheckAppSystemizedChannel.send(Unit)

        return@withContext
    }

    suspend fun unSystemize() = withContext(Dispatchers.IO) {
        if (!isAppSystemized()) return@withContext

        withWritableSystem {

            // Delete APK
            su("rm -r $APK_TARGET_DIR")

            // Permissions file should be named "privapp-permissions-<package_name>.xml"
            val permissionFileName = PERMISSIONS_FILE_NAME.format(packageName)

            // Delete permissions file
            su("rm $PERMISSIONS_TARGET_DIR/$permissionFileName")
        }

        reCheckAppSystemizedChannel.send(Unit)

        return@withContext
    }

    private suspend fun isAppSystemized(): Boolean = withContext(Dispatchers.IO) {

        val apkFile = "$APK_TARGET_DIR/$APK_NAME"
        val permissionFile = "$PERMISSIONS_TARGET_DIR/${PERMISSIONS_FILE_NAME.format(packageName)}"

        return@withContext SuFile(apkFile).exists() && SuFile(permissionFile).exists()
    }

    private suspend fun withWritableSystem(block: suspend () -> Unit) {

        // Mount System as Read Write
        su("mount -o rw,remount /")

        // Execute commands
        block()

        // Mount System as Read only
        su("mount -o remount,ro /")
    }

    private suspend fun installAPK(currentApkLocation: String) {

        // App needs to reside in an individual directory in /system/priv-app
        su("mkdir -p $APK_TARGET_DIR")

        // Copy APK to its own directory in /system/priv-app
        su("cp $currentApkLocation $APK_TARGET_DIR/$APK_NAME")

        // Fix permissions
        su("chmod 755 $APK_TARGET_DIR")
        su("chmod 644 $APK_TARGET_DIR/$APK_NAME")
    }

    private suspend fun installPermissions(
        permissions: List<String>,
        tmpDir: File
    ) {

        // Permissions file should be named "privapp-permissions-<package_name>.xml"
        val permissionFileName = PERMISSIONS_FILE_NAME.format(packageName)

        // Generate Permissions file
        createPermissionsFile(permissionFileName, permissions, tmpDir)

        // Move file to /system/etc/permissions/
        su("mv $tmpDir/$permissionFileName $PERMISSIONS_TARGET_DIR")

        // Fix permissions
        su("chmod 644 $PERMISSIONS_TARGET_DIR/$permissionFileName")
    }

    private fun createPermissionsFile(
        permissionFileName: String,
        permissions: List<String>,
        tmpDir: File
    ) {

        val permissionsList = permissions
            .joinToString("\n") { """        <permission name="$it" />""" }

        val permissionsText = """
            |<permissions>
            |
            |    <privapp-permissions package="com.redridgeapps.callrecorder">
            |$permissionsList
            |    </privapp-permissions>
            |
            |</permissions>
            |
        """.trimMargin()

        File("$tmpDir/$permissionFileName").writeText(permissionsText)
    }

    private suspend fun su(vararg commands: String): Shell.Result =
        suspendCoroutine { continuation ->
            Shell.su(*commands).submit { result ->

                if (result.isSuccess) {
                    continuation.resume(result)
                } else {
                    val exception = CommandFailedException(*commands, result = result)
                    continuation.resumeWithException(exception)
                }
            }
        }

    class CommandFailedException(
        vararg commands: String,
        result: Shell.Result
    ) : Exception(
        """libsu: Shell command failed!
            |Command: ${commands.contentToString()}
            |Output: ${result.out.ifEmpty { "null" }}
            |Error: ${result.err.ifEmpty { "null" }}
        """.trimMargin()
    )

    class Initializer @Inject constructor() : StartupInitializer {

        override fun initialize(context: Context) {
            Shell.Config.setFlags(Shell.FLAG_REDIRECT_STDERR)
            Shell.Config.verboseLogging(false)
            Shell.Config.setTimeout(10)
        }
    }
}

// APK
const val SYSTEM_PRIV_APP = "/system/priv-app"
const val APP_DIR_NAME = "CallRecorder"
const val APK_NAME = "$APP_DIR_NAME.apk"
const val APK_TARGET_DIR = "$SYSTEM_PRIV_APP/$APP_DIR_NAME"

// Permissions
const val PERMISSIONS_FILE_NAME = "privapp-permissions-%s.xml"
const val PERMISSIONS_TARGET_DIR = "/system/etc/permissions"
