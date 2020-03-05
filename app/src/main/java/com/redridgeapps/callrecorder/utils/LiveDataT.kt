package com.redridgeapps.callrecorder.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import com.redridgeapps.repository.ILiveData
import kotlinx.coroutines.flow.Flow

class LiveDataT<T : Any?>(
    private val liveData: LiveData<T>
) : ILiveData<T> {

    private val observerMap = mutableMapOf<(T) -> Unit, Observer<T>>()

    override val value = liveData.value

    override fun observeForever(block: (T) -> Unit) {
        val observer = Observer<T> { block(it) }
        observerMap[block] = observer
        liveData.observeForever(observer)
    }

    override fun removeObserver(block: (T) -> Unit) {
        val observer = observerMap[block]
        if (observer != null) liveData.removeObserver(observer)
    }
}

fun <T : Any> Flow<T>.asILiveData(): LiveDataT<T> {
    return LiveDataT(asLiveData())
}
