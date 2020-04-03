package com.redridgeapps.callrecorder.utils

import java.nio.file.Path

val Path.extension: String
    get() = fileName.toString().substringAfterLast('.', "")

val Path.nameWithoutExtension: String
    get() = fileName.toString().substringBeforeLast(".")
