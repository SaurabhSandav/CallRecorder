package com.redridgeapps.repository.viewmodel

import com.redridgeapps.repository.uimodel.ISystemizerUIModel

interface ISystemizerViewModel : ViewModelMarker {

    fun setModel(newModel: ISystemizerUIModel)

    fun systemize()

    fun unSystemize()
}
