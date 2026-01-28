package com.example.myapplication.domain.repository

import com.example.myapplication.data.remote.RetrofitClient
import com.example.myapplication.data.remote.dto.EnviarMensajeGrupalRequest
import com.example.myapplication.data.remote.dto.EnviarMensajePrivadoRequest
import com.example.myapplication.data.remote.dto.MensajeResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repositorio para gestionar mensajes con API REST
 */
class MensajeRepository {

    private val apiService = RetrofitClient.apiService

    /**
     * Enviar mensaje grupal
     */
    suspend fun enviarMensajeGrupal(
        contenido: String,
        imagenPath: String = ""
    ): Result<MensajeResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            val token = RetrofitClient.getAuthToken() ?: ""

            val request = EnviarMensajeGrupalRequest(
                contenido = contenido,
                imagenUrl = imagenPath
            )

            val response = apiService.enviarMensajeGrupal("Bearer $token", request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al enviar mensaje"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    /**
     * Obtener mensajes grupales
     */
    suspend fun obtenerMensajesGrupales(): Result<List<MensajeResponse>> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val token = RetrofitClient.getAuthToken() ?: ""
                val response = apiService.obtenerMensajesGrupales("Bearer $token")

                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error al obtener mensajes"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    /**
     * Enviar mensaje privado
     */
    suspend fun enviarMensajePrivado(
        destinatarioId: Long,
        contenido: String,
        imagenPath: String = ""
    ): Result<MensajeResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            val token = RetrofitClient.getAuthToken() ?: ""

            val request = EnviarMensajePrivadoRequest(
                destinatarioId = destinatarioId,
                contenido = contenido,
                imagenUrl = imagenPath
            )

            val response = apiService.enviarMensajePrivado("Bearer $token", request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al enviar mensaje"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    /**
     * Obtener mensajes privados
     */
    suspend fun obtenerMensajesPrivados(
        otroUsuarioId: Long
    ): Result<List<MensajeResponse>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val token = RetrofitClient.getAuthToken() ?: ""
            val response = apiService.obtenerMensajesPrivados("Bearer $token", otroUsuarioId)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al obtener mensajes"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}