package com.redridgeapps.mp3encoder

// Used in rust through JNI
@Suppress("unused")
class LameException(errMsg: String) : Exception(errMsg)