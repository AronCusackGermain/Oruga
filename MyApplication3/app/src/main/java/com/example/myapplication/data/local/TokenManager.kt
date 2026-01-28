package com.example.myapplication.data.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

/**
 * TokenManager - Gestiona el token JWT de autenticación
 *
 * Usa SharedPreferences en lugar de DataStore para simplicidad.
 * Si prefieres DataStore, el código es similar pero con corutinas.
 */
class TokenManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    companion object {
        private const val TAG = "TokenManager"
        private const val PREFS_NAME = "auth_prefs"
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_EMAIL = "user_email"
        private const val KEY_USERNAME = "username"
        private const val KEY_IS_MODERATOR = "is_moderator"
    }

    /**
     * Guardar token y datos del usuario
     */
    fun saveAuthData(
        token: String,
        userId: Long? = null,
        email: String? = null,
        username: String? = null,
        isModerator: Boolean = false
    ) {
        Log.d(TAG, "═══════════════════════════════════════")
        Log.d(TAG, "💾 Guardando datos de autenticación")
        Log.d(TAG, "Token (primeros 30 chars): ${token.take(30)}...")
        Log.d(TAG, "UserID: $userId")
        Log.d(TAG, "Email: $email")
        Log.d(TAG, "Username: $username")
        Log.d(TAG, "isModerator: $isModerator")

        prefs.edit().apply {
            putString(KEY_TOKEN, token)
            userId?.let { putLong(KEY_USER_ID, it) }
            email?.let { putString(KEY_EMAIL, it) }
            username?.let { putString(KEY_USERNAME, it) }
            putBoolean(KEY_IS_MODERATOR, isModerator)
            apply()
        }

        // ✅ Verificar que se guardó correctamente
        val savedToken = getToken()
        if (savedToken != null) {
            Log.d(TAG, "✅ Token guardado y verificado correctamente")
        } else {
            Log.e(TAG, "❌ ERROR: Token NO se guardó correctamente")
        }
        Log.d(TAG, "═══════════════════════════════════════")
    }

    /**
     * Obtener token JWT
     */
    fun getToken(): String? {
        val token = prefs.getString(KEY_TOKEN, null)

        if (token != null && token.isNotEmpty()) {
            Log.d(TAG, "🔑 Token obtenido: ${token.take(30)}...")
        } else {
            Log.w(TAG, "⚠️ Token NO encontrado o vacío")
        }

        return token
    }

    /**
     * Obtener ID del usuario
     */
    fun getUserId(): Long? {
        val userId = prefs.getLong(KEY_USER_ID, -1L)
        return if (userId != -1L) userId else null
    }

    /**
     * Obtener email del usuario
     */
    fun getEmail(): String? {
        return prefs.getString(KEY_EMAIL, null)
    }

    /**
     * Obtener username
     */
    fun getUsername(): String? {
        return prefs.getString(KEY_USERNAME, null)
    }

    /**
     * Verificar si el usuario es moderador
     */
    fun isModerator(): Boolean {
        return prefs.getBoolean(KEY_IS_MODERATOR, false)
    }

    /**
     * Verificar si hay token guardado
     */
    fun hasToken(): Boolean {
        val token = getToken()
        val hasToken = token != null && token.isNotEmpty()
        Log.d(TAG, "🔍 ¿Tiene token? $hasToken")
        return hasToken
    }

    /**
     * Limpiar todos los datos (logout)
     */
    fun clearAuthData() {
        Log.d(TAG, "🗑️ Limpiando todos los datos de autenticación")
        prefs.edit().clear().apply()

        // Verificar que se limpió
        val tokenAfterClear = getToken()
        if (tokenAfterClear == null) {
            Log.d(TAG, "✅ Datos limpiados correctamente")
        } else {
            Log.e(TAG, "❌ ERROR: Datos NO se limpiaron correctamente")
        }
    }

    /**
     * Actualizar solo el token (útil para refresh token)
     */
    fun updateToken(newToken: String) {
        Log.d(TAG, "🔄 Actualizando token")
        Log.d(TAG, "Nuevo token (primeros 30 chars): ${newToken.take(30)}...")

        prefs.edit().putString(KEY_TOKEN, newToken).apply()

        Log.d(TAG, "✅ Token actualizado")
    }
}