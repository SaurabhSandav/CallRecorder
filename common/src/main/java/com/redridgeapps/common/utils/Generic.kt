package com.redridgeapps.common.utils

import kotlin.math.ln
import kotlin.math.pow

fun humanReadableByteCount(
    bytes: Long,
    isSpeed: Boolean = false,
    si: Boolean = false,
): String {
    val unit = if (si) 1000 else 1024
    val speed = if (isSpeed) "/s" else ""

    if (bytes < unit) return "$bytes B$speed"

    val exp = (ln(bytes.toDouble()) / ln(unit.toDouble())).toInt()
    val pre = (if (si) "kMGTPE" else "KMGTPE")[exp - 1] + if (si) "" else "i"

    return String.format(
        "%.1f %sB%s",
        bytes / unit.toDouble().pow(exp.toDouble()),
        pre,
        speed
    )
}
