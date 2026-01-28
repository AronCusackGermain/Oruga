package com.example.myapplication.domain.repository

import com.example.myapplication.data.remote.RetrofitClient
import com.example.myapplication.data.remote.dto.*
import com.example.myapplication.utils.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UsuarioRepository {

    private val apiService = RetrofitClient.apiService

    /**
     * Registrar usuario con manejo de errores mejorado
     */
    suspend fun registrarUsuario(
        email: String,
        password: String,
        nombreUsuario: String,
        codigoModerador: String = ""
    ): Result<AuthResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            val request = RegisterRequest(
                email = email,
                password = password,
                nombreUsuario = nombreUsuario,
                codigoModerador = codigoModerador
            )

            val response = apiService.registrar(request)

            // Usar NetworkUtils para procesar la respuesta
            val result = NetworkUtils.processResponse(response)

            result.onSuccess { authResponse ->
                // Guardar token en TokenManager
                RetrofitClient.getTokenManager()?.saveAuthData(
                    token = authResponse.token,
                    userId = authResponse.id,
                    email = authResponse.email,
                    username = authResponse.nombreUsuario,
                    isModerator = authResponse.esModerador
                )
            }

            result
        } catch (e: Exception) {
            val errorMessage = NetworkUtils.getErrorMessage(e)
            Result.failure(Exception(errorMessage))
        }
    }

    /**
     * Login con manejo de errores mejorado
     */
    suspend fun login(
        email: String,
        password: String
    ): Result<AuthResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            val request = LoginRequest(email, password)
            val response = apiService.login(request)

            val result = NetworkUtils.processResponse(response)

            result.onSuccess { authResponse ->
                RetrofitClient.getTokenManager()?.saveAuthData(
                    token = authResponse.token,
                    userId = authResponse.id,
                    email = authResponse.email,
                    username = authResponse.nombreUsuario,
                    isModerator = authResponse.esModerador
                )
            }

            result
        } catch (e: Exception) {
            val errorMessage = NetworkUtils.getErrorMessage(e)
            Result.failure(Exception(errorMessage))
        }
    }

    /**
     * Obtener perfil con reintentos automáticos
     */
    suspend fun obtenerPerfil(): Result<UsuarioResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            val token = getToken()
            val response = apiService.obtenerPerfil(token)
            NetworkUtils.processResponse(response)
        } catch (e: Exception) {
            // Si es error de autenticación, limpiar token
            if (NetworkUtils.isAuthError(e)) {
                RetrofitClient.clearAuthToken()
            }
            val errorMessage = NetworkUtils.getErrorMessage(e)
            Result.failure(Exception(errorMessage))
        }
    }

    /**
     * Obtener todos los usuarios
     */
    suspend fun obtenerUsuarios(): Result<List<UsuarioResponse>> = withContext(Dispatchers.IO) {
        return@withContext try {
            val token = getToken()
            val response = apiService.obtenerUsuarios(token)
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
     * Actualizar perfil
     */
    suspend fun actualizarPerfil(
        descripcion: String?,
        urlFotoPerfil: String?
    ): Result<UsuarioResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            val token = getToken()
            val request = ActualizarPerfilRequest(descripcion, urlFotoPerfil)
            val response = apiService.actualizarPerfil(token, request)
            NetworkUtils.processResponse(response)
        } catch (e: Exception) {
            val errorMessage = NetworkUtils.getErrorMessage(e)
            Result.failure(Exception(errorMessage))
        }
    }

    /**
     * Conectar cuentas externas
     */
    suspend fun conectarCuentasExternas(
        steamId: String?,
        discordId: String?
    ): Result<UsuarioResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            val token = getToken()
            val response = apiService.conectarCuentasExternas(token, steamId, discordId)
            NetworkUtils.processResponse(response)
        } catch (e: Exception) {
            val errorMessage = NetworkUtils.getErrorMessage(e)
            Result.failure(Exception(errorMessage))
        }
    }

    /**
     * Logout
     */
    suspend fun logout(): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val token = getToken()
            apiService.logout(token)
            RetrofitClient.clearAuthToken()
            Result.success(Unit)
        } catch (e: Exception) {
            // Limpiar token aunque falle la petición
            RetrofitClient.clearAuthToken()
            Result.success(Unit)
        }
    }

    /**
     * Obtener token formateado
     */
    fun getToken(): String {
        val token = RetrofitClient.getAuthToken()
        return if (token != null) "Bearer $token" else ""
    }
}
