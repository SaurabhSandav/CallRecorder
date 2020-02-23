import java.io.File
import java.util.concurrent.TimeUnit

private const val ADB = "/opt/Android/Sdk/platform-tools/adb"
private val ADB_SH = "$ADB shell su -c".split(" ")

internal class TerminalTools(private val workingDir: File) {

    val output = StringBuilder()

    fun adb(command: String) = (listOf(ADB) + command.split(" ")).runCommand()

    fun adbSU(command: String) = (ADB_SH + command).runCommand()

    private fun List<String>.runCommand(): String {

        val process = ProcessBuilder(this)
            .directory(workingDir)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectErrorStream(true)
            .start()

        process.waitFor(10, TimeUnit.SECONDS)

        val commandOutput = process.inputStream.bufferedReader().readText()

        if (commandOutput.isNotBlank())
            output.append(commandOutput)

        return commandOutput
    }
}
