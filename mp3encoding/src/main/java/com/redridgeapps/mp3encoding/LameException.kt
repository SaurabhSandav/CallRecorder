package com.redridgeapps.mp3encoding

// Used in rust through JNI
@Suppress("unused")
class LameException(errMsg: String) : Exception(errMsg)