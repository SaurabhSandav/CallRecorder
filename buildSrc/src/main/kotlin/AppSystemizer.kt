import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

// Customize
const val APP_PACKAGE = "com.redridgeapps.callrecorder"
const val APP_DIR_NAME = "CallRecorder"
const val MAIN_ACTIVITY = "MainActivity"

abstract class AppSystemizer : DefaultTask() {

    private val terminalTools = TerminalTools(project.rootDir)
    private val manifestParser = ManifestParser(project.projectDir.path)

    @TaskAction
    fun install() {

        manifestParser.parse()

        withWritableSystem {
            withTmpDir {
                installAPK()
                installPermissions()
            }
        }

        runApp()

        println(terminalTools.output)
    }

    private fun withWritableSystem(block: () -> Unit) = with(terminalTools) {

        // Mount System as Read Write
        adbSU("mount -o rw,remount /")

        // Execute commands
        block()

        // Mount System as Read only
        adbSU("mount -o remount,ro /")
    }

    private fun withTmpDir(block: () -> Unit) = with(terminalTools) {

        // ADB can't push to /system/priv-app directly.
        // Need to copy to a tmp directory first and use "mv" from there.

        // Make a tmp directory
        adbSU("mkdir -p $TMP_DIR")

        // Execute commands
        block()

        // Delete tmp directory
        adbSU("rmdir $TMP_DIR")
    }

    private fun installAPK() = with(terminalTools) {

        // Push APK to tmp and rename to "<app_name>.apk"
        adb("push ${project.buildDir}/$FRESH_APK $TMP_DIR/$APK_NAME")

        // App needs to reside in an individual directory in /system/priv-app
        adbSU("mkdir -p $APK_TARGET_DIR")

        // Move APK to its own directory in /system/priv-app
        adbSU("mv $TMP_DIR/$APK_NAME $APK_TARGET_DIR/$APK_NAME")

        // Fix permissions
        adbSU("chmod 755 $APK_TARGET_DIR")
        adbSU("chmod 644 $APK_TARGET_DIR/$APK_NAME")
    }

    private fun installPermissions() = with(terminalTools) {

        updatePermissionsFile()

        // Push permissions file to device
        adb("push ${project.rootDir}/$PERMISSIONS_FILE_NAME $TMP_DIR/$PERMISSIONS_FILE_NAME")

        // Move file to /system/etc/permissions/
        adbSU("mv $TMP_DIR/$PERMISSIONS_FILE_NAME $PERMISSIONS_TARGET_DIR")

        // Fix permissions
        adbSU("chmod 644 $PERMISSIONS_TARGET_DIR/$PERMISSIONS_FILE_NAME")
    }

    private fun updatePermissionsFile() {

        val permissionsList = manifestParser.permissions
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

        File("${project.rootDir}/$PERMISSIONS_FILE_NAME").writeText(permissionsText)
    }

    private fun runApp() = with(terminalTools) {

        // Stop the app
        adb("shell am force-stop $APP_PACKAGE")

        // Re execute the app
        adb(
            "shell am start -n $APP_PACKAGE/$APP_PACKAGE.$MAIN_ACTIVITY" +
                    " -a android.intent.action.MAIN -c android.intent.category.LAUNCHER"
        )
    }
}

const val TMP_DIR = "/sdcard/tmp"

// APK
const val FRESH_APK = "/outputs/apk/debug/app-debug.apk"
const val APK_NAME = "$APP_DIR_NAME.apk"
const val SYSTEM_PRIV_APP = "/system/priv-app"
const val APK_TARGET_DIR = "$SYSTEM_PRIV_APP/$APP_DIR_NAME"

// Permissions
const val PERMISSIONS_FILE_NAME = "privapp-permissions-$APP_PACKAGE.xml"
const val PERMISSIONS_TARGET_DIR = "/system/etc/permissions"
