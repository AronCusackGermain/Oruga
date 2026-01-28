package com.example.myapplication.domain.repository

import com.example.myapplication.data.remote.RetrofitClient
import com.example.myapplication.data.remote.dto.*
import com.example.myapplication.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repositorio para funciones de moderación
 * Solo accesible por usuarios con rol de moderador
 */
class ModeracionRepository {

    private val apiService = RetrofitClient.apiService

    /**
     * Verifica si el usuario actual es moderador
     */
    fun esModerador(): Boolean {
        return RetrofitClient.getTokenManager()?.isModerator() ?: false
    }

    /**
     * Banear un usuario
     */
    suspend fun banearUsuario(
        usuarioId: Long,
        razon: String,
        duracionDias: Int? = null
    ): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            if (!esModerador()) {
                return@withContext Result.failure(Exception("No tienes permisos de moderador"))
            }

            val token = getToken()
            val request = BanearUsuarioRequest(
                razon = razon,
                duracionDias = duracionDias
            )

            val response = apiService.banearUsuario(token, usuarioId, request)

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
     * Desbanear un usuario
     */
    suspend fun desbanearUsuario(
        usuarioId: Long
    ): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            if (!esModerador()) {
                return@withContext Result.failure(Exception("No tienes permisos de moderador"))
            }

            val token = getToken()
            val response = apiService.desbanearUsuario(token, usuarioId)

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
     * Obtener usuarios baneados
     */
    suspend fun obtenerUsuariosBaneados(): Result<List<UsuarioResponse>> = withContext(Dispatchers.IO) {
        return@withContext try {
            if (!esModerador()) {
                return@withContext Result.failure(Exception("No tienes permisos de moderador"))
            }

            val token = getToken()
            val response = apiService.obtenerUsuariosBaneados(token)
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
     * Eliminar una publicación como moderador
     */
    suspend fun eliminarPublicacion(
        publicacionId: Long,
        razon: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            if (!esModerador()) {
                return@withContext Result.failure(Exception("No tienes permisos de moderador"))
            }

            val token = getToken()
            val request = EliminarContenidoRequest(razon = razon)
            val response = apiService.eliminarPublicacionPorModerador(token, publicacionId, request)

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
     * Eliminar un comentario como moderador
     */
    suspend fun eliminarComentario(
        comentarioId: Long,
        razon: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            if (!esModerador()) {
                return@withContext Result.failure(Exception("No tienes permisos de moderador"))
            }

            val token = getToken()
            val request = EliminarContenidoRequest(razon = razon)
            val response = apiService.eliminarComentarioPorModerador(token, comentarioId, request)

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
     * Crear un reporte
     */
    suspend fun crearReporte(
        tipoReporte: String,
        idContenido: Long,
        razon: String,
        descripcion: String
    ): Result<ReporteResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            val token = getToken()
            val request = CrearReporteRequest(
                tipoReporte = tipoReporte,
                idContenido = idContenido,
                razon = razon,
                descripcion = descripcion
            )

            val response = apiService.crearReporte(token, request)
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
     * Obtener todos los reportes (solo moderadores)
     */
    suspend fun obtenerReportes(): Result<List<ReporteResponse>> = withContext(Dispatchers.IO) {
        return@withContext try {
            if (!esModerador()) {
                return@withContext Result.failure(Exception("No tienes permisos de moderador"))
            }

            val token = getToken()
            val response = apiService.obtenerReportes(token)
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
     * Obtener reportes pendientes (solo moderadores)
     */
    suspend fun obtenerReportesPendientes(): Result<List<ReporteResponse>> = withContext(Dispatchers.IO) {
        return@withContext try {
            if (!esModerador()) {
                return@withContext Result.failure(Exception("No tienes permisos de moderador"))
            }

            val token = getToken()
            val response = apiService.obtenerReportesPendientes(token)
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
     * Resolver un reporte
     */
    suspend fun resolverReporte(
        reporteId: Long,
        aceptado: Boolean,
        accionTomada: String
    ): Result<ReporteResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            if (!esModerador()) {
                return@withContext Result.failure(Exception("No tienes permisos de moderador"))
            }

            val token = getToken()
            val request = ResolverReporteRequest(
                aceptado = aceptado,
                accionTomada = accionTomada
            )

            val response = apiService.resolverReporte(token, reporteId, request)
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
     * Obtener estadísticas generales (solo moderadores)
     */
    suspend fun obtenerEstadisticas(): Result<EstadisticasResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            if (!esModerador()) {
                return@withContext Result.failure(Exception("No tienes permisos de moderador"))
            }

            val token = getToken()
            val response = apiService.obtenerEstadisticas(token)
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
     * Obtener token formateado
     */
    private fun getToken(): String {
        val token = RetrofitClient.getAuthToken()
        return if (token != null) "Bearer $token" else ""
    }
}

/**
 * Enumeración para tipos de reporte
 */
object TipoReporte {
    const val USUARIO = "USUARIO"
    const val PUBLICACION = "PUBLICACION"
    const val COMENTARIO = "COMENTARIO"
    const val MENSAJE = "MENSAJE"
}