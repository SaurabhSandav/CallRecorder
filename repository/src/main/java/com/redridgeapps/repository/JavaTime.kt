package com.redridgeapps.repository

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

fun Instant.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.ofInstant(this, ZoneId.systemDefault())
}

fun Instant.toLocalDate(): LocalDate {
    return toLocalDateTime().toLocalDate()
}
