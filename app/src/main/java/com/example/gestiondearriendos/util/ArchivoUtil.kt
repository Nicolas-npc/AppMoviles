package com.example.gestiondearriendos.util

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object ArchivoUtil {

    fun crearArchivoImagen(context: Context): File{

        val carpeta = File(context.filesDir,"comprobantes")
        if (!carpeta.exists()) carpeta.mkdirs()
        return File(carpeta, "foto_${System.currentTimeMillis()}.jpg")
    }

    fun obtenerUri(context: Context,archivo: File): Uri{
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            archivo
        )
    }

}