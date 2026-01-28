package com.example.myapplication.utils

import com.example.myapplication.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Utilidad para probar la conexión con el backend
 */
object ConnectionTester {

    /**
     * Probar conexión básica al servidor
     */
    suspend fun testConnection(): TestResult = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = RetrofitClient.apiService.obtenerPublicaciones("Bearer test")

            when {
                response.code() == 401 -> TestResult.Success(
                    "✅ Servidor respondió (esperaba 401 - OK)"
                )
                response.isSuccessful -> TestResult.Success(
                    "✅ Conexión exitosa al servidor"
                )
                else -> TestResult.Warning(
                    "⚠️ Servidor responde pero con código ${response.code()}"
                )
            }
        } catch (e: Exception) {
            val errorMsg = NetworkUtils.getErrorMessage(e)
            TestResult.Error("❌ Error de conexión: $errorMsg")
        }
    }

    /**
     * Probar autenticación completa
     */
    suspend fun testAuthentication(
        email: String,
        password: String
    ): TestResult = withContext(Dispatchers.IO) {
        return@withContext try {
            val apiService = RetrofitClient.apiService
            val response = apiService.login(
                com.example.myapplication.data.remote.dto.LoginRequest(email, password)
            )

            if (response.isSuccessful && response.body() != null) {
                TestResult.Success("✅ Autenticación exitosa")
            } else {
                TestResult.Error("❌ Credenciales inválidas")
            }
        } catch (e: Exception) {
            val errorMsg = NetworkUtils.getErrorMessage(e)
            TestResult.Error("❌ Error: $errorMsg")
        }
    }

    /**
     * Probar endpoint de publicaciones
     */
    suspend fun testPublicaciones(token: String): TestResult = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = RetrofitClient.apiService.obtenerPublicaciones("Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                val count = response.body()!!.size
                TestResult.Success("✅ $count publicaciones obtenidas")
            } else {
                TestResult.Error("❌ Error al obtener publicaciones")
            }
        } catch (e: Exception) {
            val errorMsg = NetworkUtils.getErrorMessage(e)
            TestResult.Error("❌ Error: $errorMsg")
        }
    }

    /**
     * Ejecutar batería completa de pruebas
     */
    suspend fun runFullTest(
        email: String? = null,
        password: String? = null
    ): List<Pair<String, TestResult>> {
        val results = mutableListOf<Pair<String, TestResult>>()

        // Test 1: Conexión básica
        results.add("Conexión al servidor" to testConnection())

        // Test 2: Autenticación (si se proporcionan credenciales)
        if (!email.isNullOrBlank() && !password.isNullOrBlank()) {
            results.add("Autenticación" to testAuthentication(email, password))

            // Test 3: Obtener publicaciones (si hay token)
            RetrofitClient.getAuthToken()?.let { token ->
                results.add("Obtener publicaciones" to testPublicaciones(token))
            }
        }

        return results
    }

    /**
     * Resultado de prueba
     */
    sealed class TestResult {
        data class Success(val message: String) : TestResult()
        data class Warning(val message: String) : TestResult()
        data class Error(val message: String) : TestResult()
    }
}