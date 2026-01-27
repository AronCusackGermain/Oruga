package com.example.myapplication.domain.repository

import com.example.myapplication.data.remote.RetrofitClient
import com.example.myapplication.data.remote.dto.LoginRequest
import com.example.myapplication.data.remote.dto.RegisterRequest
import com.example.myapplication.data.remote.dto.AuthResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UsuarioRepository {

    private val apiService = RetrofitClient.apiService

    // Guardar token en SharedPreferences
    private var token: String? = null

    /**
     * Registrar usuario usando API
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

            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                token = authResponse.token
                Result.success(authResponse)
            } else {
                val errorMsg = when (response.code()) {
                    400 -> "Datos inválidos"
                    409 -> "El email ya está registrado"
                    else -> "Error: ${response.message()}"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    /**
     * Login usando API
     */
    suspend fun login(
        email: String,
        password: String
    ): Result<AuthResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            val request = LoginRequest(email, password)
            val response = apiService.login(request)

            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                token = authResponse.token
                Result.success(authResponse)
            } else {
                val errorMsg = when (response.code()) {
                    401 -> "Email o contraseña incorrectos"
                    403 -> "Usuario baneado"
                    else -> "Error: ${response.message()}"
                }
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    /**
     * Obtener token guardado
     */
    fun getToken(): String {
        return "Bearer $token"
    }

    /**
     * Logout
     */
    fun logout() {
        token = null
    }
}