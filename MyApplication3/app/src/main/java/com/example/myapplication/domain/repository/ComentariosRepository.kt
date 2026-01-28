package com.example.myapplication.domain.repository

import com.example.myapplication.data.remote.RetrofitClient
import com.example.myapplication.data.remote.dto.*
import com.example.myapplication.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repositorio para gestionar comentarios en publicaciones
 */
class ComentarioRepository {

    private val apiService = RetrofitClient.apiService

    /**
     * Crear un nuevo comentario
     */
    suspend fun crearComentario(
        publicacionId: Long,
        contenido: String
    ): Result<ComentarioResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            if (contenido.isBlank()) {
                return@withContext Result.failure(Exception("El comentario no puede estar vacío"))
            }

            val token = getToken()
            val request = CrearComentarioRequest(
                publicacionId = publicacionId,
                contenido = contenido
            )

            val response = apiService.crearComentario(token, request)
            NetworkUtils.processResponse(response)
        } catch (e: Exception) {
            if (NetworkUtils.isAuthError(e)) {
                RetrofitClient.clearAuthToken()
            }
            val errorMessage = NetworkUtils.getErrorMessage(e)
            Result.failure(Exception(errorMessage))
        }
    }

    /**
     * Obtener comentarios de una publicación
     */
    suspend fun obtenerComentarios(
        publicacionId: Long
    ): Result<List<ComentarioResponse>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val token = getToken()
            val response = apiService.obtenerComentarios(token, publicacionId)
            NetworkUtils.processResponse(response)
        } catch (e: Exception) {
            if (NetworkUtils.isAuthError(e)) {
                RetrofitClient.clearAuthToken()
            }
            val errorMessage = NetworkUtils.getErrorMessage(e)
            Result.failure(Exception(errorMessage))
        }
    }

    /**
     * Eliminar un comentario
     */
    suspend fun eliminarComentario(
        comentarioId: Long
    ): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val token = getToken()
            val response = apiService.eliminarComentario(token, comentarioId)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorMessage = NetworkUtils.getErrorMessage(
                    Exception("Error ${response.code()}: ${response.message()}")
                )
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            if (NetworkUtils.isAuthError(e)) {
                RetrofitClient.clearAuthToken()
            }
            val errorMessage = NetworkUtils.getErrorMessage(e)
            Result.failure(Exception(errorMessage))
        }
    }

    /**
     * Contar comentarios de una publicación
     */
    suspend fun contarComentarios(publicacionId: Long): Int {
        return try {
            val result = obtenerComentarios(publicacionId)
            result.getOrNull()?.size ?: 0
        } catch (e: Exception) {
            0
        }
    }

    /**
     * Obtener token formateado
     */
    private fun getToken(): String {
        val token = RetrofitClient.getAuthToken()
        return if (token != null) "Bearer $token" else ""
    }
}