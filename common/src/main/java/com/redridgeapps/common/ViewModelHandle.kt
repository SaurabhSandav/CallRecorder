package com.redridgeapps.common

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import timber.log.Timber
import java.io.Closeable

interface ViewModelHandle {

    val savedStateHandle: SavedStateHandle

    val coroutineScope: CoroutineScope

    fun onInit(initBlock: () -> Unit)

    fun onClear(clearBlock: () -> Unit)
}

interface InitializerViewModelHandle : ViewModelHandle {

    fun init()
}

private class ViewModelHandleImpl(
    override val savedStateHandle: SavedStateHandle,
    viewModelScope: CoroutineScope,
) : InitializerViewModelHandle, Closeable {

    private var initBlockList: MutableList<() -> Unit>? = mutableListOf()
    private val clearBlockList = mutableListOf<() -> Unit>()

    override val coroutineScope: CoroutineScope = viewModelScope

    override fun init() {
        initBlockList?.forEach { it() } ?: error("init() should only be called once")
        initBlockList = null
    }

    override fun onInit(initBlock: () -> Unit) {
        initBlockList?.add(initBlock)
            ?: error("onInit() should only be called in a constructor block")
    }

    override fun onClear(clearBlock: () -> Unit) {
        clearBlockList.add(clearBlock)
    }

    override fun close() {
        clearBlockList.forEach { it() }
        Timber.d("Clearing")
    }
}

fun ViewModel.createViewModelHandle(savedStateHandle: SavedStateHandle): InitializerViewModelHandle {

    var viewModelClass: Class<*> = this.javaClass
    while (viewModelClass != ViewModel::class.java) {
        viewModelClass = viewModelClass.superclass
    }

    val setTagIfAbsent = viewModelClass.getDeclaredMethod(
        "setTagIfAbsent",
        String::class.java,
        Object::class.java
    )
    setTagIfAbsent.isAccessible = true

    val viewModelHandle = ViewModelHandleImpl(savedStateHandle, viewModelScope)

    return setTagIfAbsent(this, "ViewModelHandleKey", viewModelHandle) as InitializerViewModelHandle
}
