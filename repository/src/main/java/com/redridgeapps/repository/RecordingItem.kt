package com.redridgeapps.repository

import java.time.Instant

data class RecordingItem(
    val id: Int,
    val name: String,
    val number: String,
    val startInstant: Instant,
    val endInstant: Instant,
    val callType: String,
    val saveFormat: String
)
