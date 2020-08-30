package com.redridgeapps.callrecorder.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppDispatchers(
    val IO: CoroutineDispatcher,
    val Default: CoroutineDispatcher,
    val Main: CoroutineDispatcher,
) {
    @Inject
    constructor() : this(
        IO = Dispatchers.IO,
        Default = Dispatchers.Default,
        Main = Dispatchers.Main
    )
}
