
package com.helpme.redciudadana.controller

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

class CamaraController(
    private val context: Context
) {

    fun crearUriFotografia(): Uri {

        val directorio = File(
            context.cacheDir,
            "reportes"
        )

        if (!directorio.exists()) {
            directorio.mkdirs()
        }

        val archivo = File.createTempFile(
            "incidente_",
            ".jpg",
            directorio
        )

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            archivo
        )
    }
}