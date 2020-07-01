package com.redridgeapps.mp3encoder.lame

// Used in Rust through JNI
@Suppress("unused")
internal class LameException(errMsg: String) : Exception(errMsg)
