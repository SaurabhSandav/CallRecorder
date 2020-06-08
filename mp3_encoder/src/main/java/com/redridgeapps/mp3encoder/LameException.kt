package com.redridgeapps.mp3encoder

// Used in Rust through JNI
@Suppress("unused")
class LameException(errMsg: String) : Exception(errMsg)
