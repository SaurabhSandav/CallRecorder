package com.redridgeapps.repository

interface ILiveData<T> {

    val value: T?

    fun observeForever(block: (T) -> Unit)

    fun removeObserver(block: (T) -> Unit)
}
