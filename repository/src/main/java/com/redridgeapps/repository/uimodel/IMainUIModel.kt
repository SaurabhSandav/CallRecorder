package com.redridgeapps.repository.uimodel

import com.redridgeapps.repository.RecordingItem

interface IMainUIModel {

    var refreshing: Boolean

    var recordingList: List<RecordingItem>
}
