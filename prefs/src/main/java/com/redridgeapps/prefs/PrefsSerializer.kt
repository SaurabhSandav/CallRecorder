package com.redridgeapps.prefs

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

// TODO Review
object PrefsSerializer : Serializer<Prefs> {

    override val defaultValue: Prefs
        get() = Prefs()

    override suspend fun readFrom(input: InputStream): Prefs {
        return when {
            input.available() != 0 -> try {
                Prefs.ADAPTER.decode(input)
            } catch (exception: IOException) {
                throw CorruptionException("Cannot read proto", exception)
            }
            else -> Prefs()
        }
    }

    override suspend fun writeTo(t: Prefs, output: OutputStream) {
        Prefs.ADAPTER.encode(output, t)
    }
}
