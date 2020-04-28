package com.redridgeapps.mp3encoding

import com.redridgeapps.repository.callutils.WavData
import java.nio.file.Path

data class EncodingJob(
    val wavData: WavData,
    val quality: Int = 2,
    val wavPath: Path,
    val mp3Path: Path
)