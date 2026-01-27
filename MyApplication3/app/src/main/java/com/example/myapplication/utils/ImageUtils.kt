package com.example.myapplication.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID

/**
 * Utilidades para manejo de imÃ¡genes
 */
object ImageUtils {

    /**
     * Guarda una imagen desde URI a almacenamiento interno
     * @return Ruta del archivo guardado
     */
    fun guardarImagenDesdeUri(
        context: Context,
        uri: Uri,
        carpeta: String = "imagenes"
    ): String? {
        return try {
            // Crear carpeta si no existe
            val directorioImagenes = File(context.filesDir, carpeta)
            if (!directorioImagenes.exists()) {
                directorioImagenes.mkdirs()
            }

            // Generar nombre Ãºnico
            val nombreArchivo = "IMG_${UUID.randomUUID()}.jpg"
            val archivoDestino = File(directorioImagenes, nombreArchivo)

            // Leer imagen desde URI
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            // Comprimir y guardar
            FileOutputStream(archivoDestino).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
            }

            // Retornar ruta relativa
            "$carpeta/$nombreArchivo"
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Carga un bitmap desde ruta interna
     */
    fun cargarImagenDesdePath(context: Context, path: String): Bitmap? {
        return try {
            val archivo = File(context.filesDir, path)
            if (archivo.exists()) {
                BitmapFactory.decodeFile(archivo.absolutePath)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Obtiene URI de archivo interno
     */
    fun obtenerUriDeArchivo(context: Context, path: String): Uri? {
        return try {
            val archivo = File(context.filesDir, path)
            if (archivo.exists()) {
                Uri.fromFile(archivo)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Elimina imagen del almacenamiento
     */
    fun eliminarImagen(context: Context, path: String): Boolean {
        return try {
            val archivo = File(context.filesDir, path)
            archivo.delete()
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Valida que sea una imagen
     */
    fun esImagenValida(uri: Uri, context: Context): Boolean {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()

            options.outWidth > 0 && options.outHeight > 0
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Convierte lista de paths a String separado por comas
     */
    fun pathsAString(paths: List<String>): String {
        return paths.joinToString(",")
    }

    /**
     * Convierte String separado por comas a lista de paths
     */
    fun stringAPaths(pathsString: String): List<String> {
        return if (pathsString.isBlank()) {
            emptyList()
        } else {
            pathsString.split(",").filter { it.isNotBlank() }
        }
    }
}
