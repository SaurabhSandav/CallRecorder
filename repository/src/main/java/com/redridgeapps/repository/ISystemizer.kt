package com.redridgeapps.repository

interface ISystemizer {

    fun isAppSystemized(): Boolean

    fun systemize(onComplete: () -> Unit)

    fun unSystemize(onComplete: () -> Unit)
}
