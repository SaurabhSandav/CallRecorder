package com.redridgeapps.callrecorder.utils

import java.nio.file.Path

val Path.extension: String
    get() = fileName.toString().substringAfterLast('.', "")

val Path.filenameWithoutExtension: String
    get() = fileName.toString().substringBeforeLast(".")

fun Path.replaceExtension(extension: String): Path {

    val originalFileName = fileName.toString()

    val newFileName = originalFileName.replaceAfterLast(
        delimiter = ".",
        replacement = extension,
        missingDelimiterValue = "$originalFileName.$extension"
    )

    return parent.resolve(newFileName)
}
