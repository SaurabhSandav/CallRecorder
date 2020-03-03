package com.redridgeapps.callrecorder.callutils

import android.content.Context
import android.os.Environment
import androidx.core.content.ContextCompat
import com.redridgeapps.repository.RecordingItem
import java.io.File

fun Context.getRecordingList(): List<RecordingItem> {

    val isExternalStorageReadable = Environment.getExternalStorageState() in
            setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
    if (!isExternalStorageReadable)
        error("External Storage is not readable")

    val externalStorageVolumes = ContextCompat.getExternalFilesDirs(this, null)
    val primaryExternalStorage = externalStorageVolumes[0]
    val saveDir = File(primaryExternalStorage, "CallRecordings")
    return saveDir.listFiles()!!.map {
        RecordingItem(it.nameWithoutExtension, it.extension)
    }
}
