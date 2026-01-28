package com.example.myapplication.domain.repository

import com.example.myapplication.data.remote.RetrofitClient
import com.example.myapplication.data.remote.dto.CrearPublicacionRequest
import com.example.myapplication.data.remote.dto.PublicacionResponse
import com.example.myapplication.utils.ImageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repositorio para gestionar publicaciones con API REST
 */
class PublicacionRepository {

    private val apiService = RetrofitClient.apiService

    /**
     * Crear publicación con imágenes
     */
    suspend fun crearPublicacion(
        titulo: String,
        contenido: String,
        imagePaths: List<String> = emptyList(),
        esAnuncio: Boolean = false
    ): Result<PublicacionResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            val token = RetrofitClient.getAuthToken() ?: ""

            // Convertir lista de paths a string separado por comas
            val imagenesUrls = ImageUtils.pathsAString(imagePaths)

            val request = CrearPublicacionRequest(
                titulo = titulo,
                contenido = contenido,
                imagenesUrls = imagenesUrls,
                esAnuncio = esAnuncio
            )

            val response = apiService.crearPublicacion("Bearer $token", request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMsg = when (response.code()) {
                    400 -> "Datos inválidos"
                    401 -> "No autorizado"
                    403 -> "No tienes permisos"
                    else -> "Error al crear publicación"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    /**
     * Obtener todas las publicaciones
     */
    suspend fun obtenerTodasPublicaciones(): Result<List<PublicacionResponse>> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val token = RetrofitClient.getAuthToken() ?: ""
                val response = apiService.obtenerPublicaciones("Bearer $token")

                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error al obtener publicaciones"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    /**
     * Obtener publicación por ID
     */
    suspend fun obtenerPublicacionPorId(id: Long): Result<PublicacionResponse> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val token = RetrofitClient.getAuthToken() ?: ""
                val response = apiService.obtenerPublicacion("Bearer $token", id)

                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Publicación no encontrada"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    /**
     * Dar like a publicación
     */
    suspend fun darLike(publicacionId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val token = RetrofitClient.getAuthToken() ?: ""
            val response = apiService.darLike("Bearer $token", publicacionId)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al dar like"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Eliminar publicación
     */
    suspend fun eliminarPublicacion(publicacionId: Long): Result<Unit> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val token = RetrofitClient.getAuthToken() ?: ""
                val response = apiService.eliminarPublicacion("Bearer $token", publicacionId)

                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Error al eliminar publicación"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    /**
     * Obtener imágenes de una publicación
     */
    fun obtenerImagenesDePublicacion(publicacion: PublicacionResponse): List<String> {
        return ImageUtils.stringAPaths(publicacion.imagenesUrls)
    }
}