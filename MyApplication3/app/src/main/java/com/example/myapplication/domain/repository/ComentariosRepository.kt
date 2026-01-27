package com.example.myapplication.domain.repository

import com.example.myapplication.data.dao.ComentarioDao
import com.example.myapplication.data.dao.PublicacionDao
import com.example.myapplication.data.models.Comentario
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio para gestionar comentarios en publicaciones
 */
class ComentarioRepository(
    private val comentarioDao: ComentarioDao,
    private val publicacionDao: PublicacionDao
) {

    /**
     * Crear un nuevo comentario
     */
    suspend fun crearComentario(
        publicacionId: Int,
        autorId: Int,
        autorNombre: String,
        contenido: String
    ): Result<Long> {
        return try {
            if (contenido.isBlank()) {
                return Result.failure(Exception("El comentario no puede estar vacío"))
            }

            val comentario = Comentario(
                publicacionId = publicacionId,
                autorId = autorId,
                autorNombre = autorNombre,
                contenido = contenido
            )

            val id = comentarioDao.insertarComentario(comentario)

            // Incrementar contador de comentarios en la publicación
            publicacionDao.incrementarComentarios(publicacionId)

            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtener comentarios de una publicación
     */
    fun obtenerComentarios(publicacionId: Int): Flow<List<Comentario>> {
        return comentarioDao.obtenerComentariosPorPublicacion(publicacionId)
    }

    /**
     * Eliminar un comentario
     */
    suspend fun eliminarComentario(comentario: Comentario): Result<Unit> {
        return try {
            comentarioDao.eliminarComentario(comentario)
            // Decrementar contador (necesitarías agregar esta función al PublicacionDao)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Contar comentarios de una publicación
     */
    suspend fun contarComentarios(publicacionId: Int): Int {
        return try {
            comentarioDao.contarComentarios(publicacionId)
        } catch (e: Exception) {
            0
        }
    }
}