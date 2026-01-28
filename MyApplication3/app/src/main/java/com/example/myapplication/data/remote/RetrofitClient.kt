package com.example.myapplication.data.remote

import android.content.Context
import android.util.Log
import com.example.myapplication.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val TAG = "RetrofitClient"

    // ⚠️ IMPORTANTE: Cambia esto por tu URL de Railway
    private const val BASE_URL = "https://oruga-production.up.railway.app/"

    // Para desarrollo con emulador:
    //private const val BASE_URL = "http://10.0.2.2:8080/"

    private var tokenManager: TokenManager? = null
    private const val MAX_RETRIES = 3
    private const val RETRY_DELAY_MS = 1000L

    /**
     * Inicializar con contexto
     */
    fun init(context: Context) {
        tokenManager = TokenManager(context.applicationContext)
        Log.d(TAG, "✅ RetrofitClient inicializado")
    }

    /**
     * Interceptor para agregar token JWT con LOGS DE DEBUGGING
     */
    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()

        // ✅ LOGS DE DEBUGGING
        Log.d(TAG, "═══════════════════════════════════════")
        Log.d(TAG, "🌐 Request: ${originalRequest.method} ${originalRequest.url}")

        // Obtener token
        val token = tokenManager?.getToken()

        // ✅ LOG DE TOKEN
        if (token != null && token.isNotEmpty()) {
            Log.d(TAG, "🔑 Token encontrado: ${token.take(30)}...")
        } else {
            Log.w(TAG, "⚠️ Token NO encontrado o vacío")
        }

        // Construir request
        val requestBuilder = originalRequest.newBuilder()

        // Agregar token si existe
        token?.takeIf { it.isNotEmpty() }?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
            Log.d(TAG, "✅ Header Authorization agregado")
        } ?: run {
            Log.w(TAG, "⚠️ Request SIN token - endpoint público?")
        }

        // Headers adicionales
        requestBuilder.addHeader("Accept", "application/json")
        requestBuilder.addHeader("Content-Type", "application/json")

        val request = requestBuilder.build()

        try {
            // Procesar request
            val response = chain.proceed(request)

            // ✅ LOG DE RESPUESTA
            Log.d(TAG, "📥 Response: ${response.code} ${response.message}")

            if (!response.isSuccessful) {
                Log.e(TAG, "❌ Error Response Code: ${response.code}")

                // Log del error body (solo primeros 500 caracteres)
                val errorBody = response.peekBody(Long.MAX_VALUE).string()
                if (errorBody.isNotEmpty()) {
                    Log.e(TAG, "❌ Error Body: ${errorBody.take(500)}")
                }
            } else {
                Log.d(TAG, "✅ Request exitoso")
            }

            Log.d(TAG, "═══════════════════════════════════════")

            return@Interceptor response

        } catch (e: Exception) {
            Log.e(TAG, "💥 Exception en request: ${e.message}", e)
            Log.d(TAG, "═══════════════════════════════════════")
            throw e
        }
    }

    /**
     * Interceptor para reintentos automáticos
     */
    private val retryInterceptor = Interceptor { chain ->
        val request = chain.request()
        var response: Response? = null
        var exception: IOException? = null

        // Intentar hasta MAX_RETRIES veces
        for (attempt in 0 until MAX_RETRIES) {
            try {
                response = chain.proceed(request)

                // Si la respuesta es exitosa o no es reintentable, retornar
                if (response.isSuccessful || !shouldRetry(response.code)) {
                    return@Interceptor response
                }

                Log.w(TAG, "🔄 Reintentando request (intento ${attempt + 1}/$MAX_RETRIES)")

                // Cerrar respuesta anterior
                response.close()

                // Esperar antes de reintentar
                if (attempt < MAX_RETRIES - 1) {
                    Thread.sleep(RETRY_DELAY_MS * (attempt + 1))
                }
            } catch (e: IOException) {
                exception = e

                Log.w(TAG, "🔄 IOException en intento ${attempt + 1}/$MAX_RETRIES: ${e.message}")

                // Esperar antes de reintentar
                if (attempt < MAX_RETRIES - 1) {
                    Thread.sleep(RETRY_DELAY_MS * (attempt + 1))
                }
            }
        }

        // Si llegamos aquí, todos los intentos fallaron
        Log.e(TAG, "❌ Todos los intentos fallaron después de $MAX_RETRIES intentos")
        response?.let { return@Interceptor it }
        throw exception ?: IOException("Error desconocido después de $MAX_RETRIES intentos")
    }

    /**
     * Determinar si un código de error debería reintentar
     */
    private fun shouldRetry(code: Int): Boolean {
        return code in listOf(408, 429, 500, 502, 503, 504)
    }

    /**
     * Logging interceptor
     */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    /**
     * Cliente OkHttp
     */
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)   // Primero logging
        .addInterceptor(authInterceptor)      // Luego auth
        .addInterceptor(retryInterceptor)     // Finalmente retry
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    /**
     * Instancia de Retrofit
     */
    val apiService: ApiService by lazy {
        Log.d(TAG, "🚀 Creando ApiService con BASE_URL: $BASE_URL")
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    /**
     * Limpiar token (logout)
     */
    fun clearAuthToken() {
        Log.d(TAG, "🗑️ Limpiando token de autenticación")
        tokenManager?.clearAuthData()
    }

    /**
     * Obtener token actual
     */
    fun getAuthToken(): String? {
        val token = tokenManager?.getToken()
        Log.d(TAG, "🔍 Obteniendo token: ${if (token != null) "encontrado" else "NULL"}")
        return token
    }

    /**
     * Obtener TokenManager
     */
    fun getTokenManager(): TokenManager? = tokenManager
}