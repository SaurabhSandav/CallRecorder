package com.redridgeapps.mp3encoder

import com.redridgeapps.wavutils.WavData
import java.nio.file.Path

data class EncodingJob(
    val wavData: WavData,
    val quality: Int = 2,
    val wavPath: Path,
    val mp3Path: Path
)