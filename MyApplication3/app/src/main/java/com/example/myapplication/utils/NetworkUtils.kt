package com.example.myapplication.utils

import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Utilidades para manejo de red y errores
 */
object NetworkUtils {

    /**
     * Procesar respuesta de Retrofit con manejo de errores
     */
    fun <T> processResponse(response: Response<T>): Result<T> {
        return try {
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val errorMessage = when (response.code()) {
                    400 -> "❌ Solicitud inválida"
                    401 -> "🔒 No autorizado - Por favor inicia sesión nuevamente"
                    403 -> "⛔ Acceso denegado - No tienes permisos"
                    404 -> "🔍 Recurso no encontrado"
                    409 -> "⚠️ Conflicto - El recurso ya existe"
                    422 -> "📝 Datos inválidos - Verifica la información"
                    429 -> "⏳ Demasiadas solicitudes - Espera un momento"
                    500 -> "💥 Error del servidor - Intenta más tarde"
                    502 -> "🌐 Gateway error - Verifica tu conexión"
                    503 -> "🚧 Servicio no disponible - Intenta más tarde"
                    else -> "❓ Error ${response.code()}: ${response.message()}"
                }
                Result.failure(ApiException(errorMessage, response.code()))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtener mensaje de error amigable
     */
    fun getErrorMessage(exception: Throwable): String {
        return when (exception) {
            is ApiException -> exception.message ?: "Error desconocido"
            is UnknownHostException -> "📡 Sin conexión a internet - Verifica tu red"
            is SocketTimeoutException -> "⏱️ Tiempo de espera agotado - Intenta nuevamente"
            is IOException -> "🔌 Error de conexión - Verifica tu internet"
            else -> "❌ Error: ${exception.message ?: "Desconocido"}"
        }
    }

    /**
     * Verificar si el error es de autenticación
     */
    fun isAuthError(exception: Throwable): Boolean {
        return when (exception) {
            is ApiException -> exception.code in listOf(401, 403)
            else -> false
        }
    }

    /**
     * Verificar si el error es recuperable (debería reintentar)
     */
    fun isRetryableError(exception: Throwable): Boolean {
        return when (exception) {
            is ApiException -> exception.code in listOf(408, 429, 500, 502, 503, 504)
            is SocketTimeoutException -> true
            is IOException -> true
            else -> false
        }
    }
}

/**
 * Excepción personalizada para errores de API
 */
class ApiException(
    message: String,
    val code: Int
) : Exception(message)