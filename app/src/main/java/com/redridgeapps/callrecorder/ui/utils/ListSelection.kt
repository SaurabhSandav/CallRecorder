package com.redridgeapps.callrecorder.ui.utils

import androidx.compose.frames.modelListOf
import androidx.compose.getValue
import androidx.compose.mutableStateOf
import androidx.compose.setValue

class ListSelection<T>(
    private val _selection: MutableList<T> = modelListOf()
) : List<T> by _selection {

    var inMultiSelectMode: Boolean by mutableStateOf(false)
        private set

    fun select(item: T) {
        when {
            inMultiSelectMode -> addOrRemoveSelection(item)
            else -> clearAndAddSelection(item)
        }
    }

    fun multiSelect(item: T) {
        inMultiSelectMode = true
        select(item)
    }

    fun clear() {
        _selection.clear()
        inMultiSelectMode = false
    }

    private fun addOrRemoveSelection(item: T) {

        when (item) {
            in _selection -> _selection.remove(item)
            else -> _selection.add(item)
        }

        if (_selection.isEmpty())
            inMultiSelectMode = false
    }

    private fun clearAndAddSelection(item: T) {
        _selection.clear()
        _selection.add(item)
    }
}