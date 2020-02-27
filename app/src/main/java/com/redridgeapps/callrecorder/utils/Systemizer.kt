package com.redridgeapps.callrecorder.utils

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.redridgeapps.repository.ISystemizer
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.io.SuFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Suppress("FunctionName")
fun AppCompatActivity.Systemizer(): Systemizer {

    val info = packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
    val permissions = info.requestedPermissions.asList()
    return Systemizer(packageName, applicationInfo.sourceDir, permissions, cacheDir, lifecycleScope)
}

class Systemizer(
    private val packageName: String,
    private val currentApkLocation: String,
    private val permissions: List<String>,
    private val tmpDir: File,
    private val coroutineScope: CoroutineScope
) : ISystemizer {

    private val outputChannel = BroadcastChannel<String>(Channel.CONFLATED)

    // TODO Implement output viewer
    val outputFlow = outputChannel.asFlow()

    // TODO Update value automatically on change
    override fun isAppSystemized(): Boolean =
        SuFile("/system/priv-app/CallRecorder/CallRecorder.apk").exists() &&
                SuFile("/system/etc/permissions/privapp-permissions-$packageName.xml").exists()

    override fun systemize(onComplete: () -> Unit) {
        coroutineScope.launch {
            systemizeS()
            onComplete()
        }
    }

    override fun unSystemize(onComplete: () -> Unit) {
        coroutineScope.launch {
            unSystemizeS()
            onComplete()
        }
    }

    private suspend fun systemizeS() {
        if (isAppSystemized()) return

        withWritableSystem {
            installAPK(currentApkLocation)
            installPermissions(permissions, tmpDir)
        }
    }

    private suspend fun unSystemizeS() {
        if (!isAppSystemized()) return

        withWritableSystem {

            // Delete APK
            su("rm -r $APK_TARGET_DIR")

            // Permissions file should be named "privapp-permissions-<package_name>.xml"
            val permissionFileName = PERMISSIONS_FILE_NAME.format(packageName)

            // Delete permissions file
            su("rm $PERMISSIONS_TARGET_DIR/$permissionFileName")
        }
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

        // Move APK to its own directory in /system/priv-app
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
                    result.out.forEach { outputChannel.offer(it) }
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
}

// APK
const val SYSTEM_PRIV_APP = "/system/priv-app"
const val APP_DIR_NAME = "CallRecorder"
const val APK_NAME = "$APP_DIR_NAME.apk"
const val APK_TARGET_DIR = "$SYSTEM_PRIV_APP/$APP_DIR_NAME"

// Permissions
const val PERMISSIONS_FILE_NAME = "privapp-permissions-%s.xml"
const val PERMISSIONS_TARGET_DIR = "/system/etc/permissions"
