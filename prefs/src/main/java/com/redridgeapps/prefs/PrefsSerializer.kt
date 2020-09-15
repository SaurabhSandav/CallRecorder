package com.redridgeapps.prefs

import androidx.datastore.CorruptionException
import androidx.datastore.Serializer
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

object PrefsSerializer : Serializer<Prefs> {

    override fun readFrom(input: InputStream): Prefs {
        return when {
            input.available() != 0 -> try {
                Prefs.ADAPTER.decode(input)
            } catch (exception: IOException) {
                throw CorruptionException("Cannot read proto", exception)
            }
            else -> Prefs()
        }
    }

    override fun writeTo(t: Prefs, output: OutputStream) {
        Prefs.ADAPTER.encode(output, t)
    }
}
