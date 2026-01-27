package com.example.myapplication.domain.repository

import com.example.myapplication.data.dao.PublicacionDao
import com.example.myapplication.data.models.Publicacion
import com.example.myapplication.utils.ImageUtils
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio para gestionar operaciones relacionadas con Publicacion
 * ACTUALIZADO: Soporte para mÃºltiples imÃ¡genes
 */
class PublicacionRepository(private val publicacionDao: PublicacionDao) {

    /**
     * Crea una nueva publicaciÃ³n con soporte de imÃ¡genes
     */
    suspend fun crearPublicacion(
        autorId: Int,
        autorNombre: String,
        titulo: String,
        contenido: String,
        imagePaths: List<String> = emptyList(), // Lista de rutas de imÃ¡genes
        esAnuncio: Boolean = false
    ): Result<Long> {
        return try {
            if (titulo.isBlank() || contenido.isBlank()) {
                return Result.failure(Exception("El tÃ­tulo y contenido son obligatorios"))
            }

            // Convertir lista de paths a string separado por comas
            val imagenesUrls = ImageUtils.pathsAString(imagePaths)
            val imagenPrincipal = imagePaths.firstOrNull() ?: ""

            val publicacion = Publicacion(
                autorId = autorId,
                autorNombre = autorNombre,
                titulo = titulo,
                contenido = contenido,
                imagenUrl = imagenPrincipal,
                imagenesUrls = imagenesUrls,
                esAnuncio = esAnuncio
            )

            val id = publicacionDao.insertarPublicacion(publicacion)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene todas las publicaciones
     */
    fun obtenerTodasPublicaciones(): Flow<List<Publicacion>> {
        return publicacionDao.obtenerTodasPublicaciones()
    }

    /**
     * Obtiene solo los anuncios
     */
    fun obtenerAnuncios(): Flow<List<Publicacion>> {
        return publicacionDao.obtenerAnuncios()
    }

    /**
     * Obtiene publicaciones de un autor especÃ­fico
     */
    fun obtenerPublicacionesPorAutor(autorId: Int): Flow<List<Publicacion>> {
        return publicacionDao.obtenerPublicacionesPorAutor(autorId)
    }

    /**
     * Obtiene una publicaciÃ³n por su ID
     */
    suspend fun obtenerPublicacionPorId(id: Int): Publicacion? {
        return try {
            publicacionDao.obtenerPublicacionPorId(id)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Incrementa los likes de una publicaciÃ³n
     */
    suspend fun darLike(publicacionId: Int): Result<Unit> {
        return try {
            publicacionDao.incrementarLikes(publicacionId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Incrementa el contador de comentarios
     */
    suspend fun agregarComentario(publicacionId: Int): Result<Unit> {
        return try {
            publicacionDao.incrementarComentarios(publicacionId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Elimina una publicaciÃ³n
     */
    suspend fun eliminarPublicacion(publicacion: Publicacion): Result<Unit> {
        return try {
            publicacionDao.eliminarPublicacion(publicacion)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene las imÃ¡genes de una publicaciÃ³n como lista
     */
    fun obtenerImagenesDePublicacion(publicacion: Publicacion): List<String> {
        return ImageUtils.stringAPaths(publicacion.imagenesUrls)
    }
}
