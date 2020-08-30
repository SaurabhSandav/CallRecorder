package com.redridgeapps.callrecorder.common.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.toJavaLocalDateTime
import java.time.format.DateTimeFormatter

fun LocalDateTime.format(dateTimeFormatter: DateTimeFormatter): String {
    return toJavaLocalDateTime().format(dateTimeFormatter)
}

fun LocalDate.format(dateTimeFormatter: DateTimeFormatter): String {
    return toJavaLocalDate().format(dateTimeFormatter)
}
