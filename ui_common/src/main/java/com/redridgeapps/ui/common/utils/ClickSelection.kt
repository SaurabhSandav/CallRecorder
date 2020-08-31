package com.redridgeapps.ui.common.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ClickSelection<T>(
    initialSelection: List<T> = emptyList(),
    inMultiSelectMode: Boolean = false,
) {

    private val _state = MutableStateFlow(SelectionState(initialSelection, inMultiSelectMode))

    val state: StateFlow<SelectionState<T>> by ::_state

    fun select(item: T) {
        when {
            _state.value.inMultiSelectMode -> addOrRemoveSelection(item)
            else -> clearAndAddSelection(item)
        }
    }

    fun multiSelect(item: T) {
        _state.value = _state.value.copy(inMultiSelectMode = true)
        select(item)
    }

    fun clear() {
        _state.value = _state.value.copy(
            selection = emptyList(),
            inMultiSelectMode = false
        )
    }

    private fun addOrRemoveSelection(item: T) {

        val selection = _state.value.selection

        val newSelection = when (item) {
            in selection -> {
                val newSelection = selection - item

                if (newSelection.isEmpty())
                    _state.value = _state.value.copy(inMultiSelectMode = false)

                newSelection
            }
            else -> selection + item
        }

        _state.value = _state.value.copy(selection = newSelection)
    }

    private fun clearAndAddSelection(item: T) {
        _state.value = _state.value.copy(selection = listOf(item))
    }
}

data class SelectionState<T>(
    val selection: List<T>,
    val inMultiSelectMode: Boolean = false,
)
