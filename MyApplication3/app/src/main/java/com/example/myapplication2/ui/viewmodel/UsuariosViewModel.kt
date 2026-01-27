package com.example.myapplication.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.dtos.UsuarioDto
import com.example.myapplication.domain.repository.OrugaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para gestión de usuarios - SOLO BACKEND
 * Ya no usa Room Database
 */
class UsuarioViewModel : ViewModel() {

    private val repository = OrugaRepository()

    // Estado del usuario actual
    private val _usuarioActual = MutableStateFlow<UsuarioDto?>(null)
    val usuarioActual: StateFlow<UsuarioDto?> = _usuarioActual.asStateFlow()

    // Token JWT
    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token.asStateFlow()

    // Lista de todos los usuarios (para chats, miembros, etc.)
    private val _todosLosUsuarios = MutableStateFlow<List<UsuarioDto>>(emptyList())
    val todosLosUsuarios: StateFlow<List<UsuarioDto>> = _todosLosUsuarios.asStateFlow()

    private val _usuariosConectados = MutableStateFlow<List<UsuarioDto>>(emptyList())
    val usuariosConectados: StateFlow<List<UsuarioDto>> = _usuariosConectados.asStateFlow()

    private val _usuariosDesconectados = MutableStateFlow<List<UsuarioDto>>(emptyList())
    val usuariosDesconectados: StateFlow<List<UsuarioDto>> = _usuariosDesconectados.asStateFlow()

    /**
     * Registrar usuario
     */
    fun registrar(
        email: String,
        password: String,
        nombreUsuario: String,
        codigoModerador: String = "",
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.register(email, password, nombreUsuario, codigoModerador)
            result.fold(
                onSuccess = { jwtResponse ->
                    // Guardar token y usuario
                    _token.value = jwtResponse.token
                    _usuarioActual.value = jwtResponse.usuario

                    // Mensaje personalizado
                    val mensaje = if (jwtResponse.usuario.esModerador) {
                        "¡Registro exitoso! Bienvenido Moderador ${jwtResponse.usuario.nombreUsuario} 🛡️"
                    } else {
                        "¡Registro exitoso! Bienvenido ${jwtResponse.usuario.nombreUsuario}"
                    }
                    onResult(true, mensaje)
                },
                onFailure = { error ->
                    onResult(false, error.message ?: "Error al registrar")
                }
            )
        }
    }

    /**
     * Login
     */
    fun login(
        email: String,
        password: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.login(email, password)
            result.fold(
                onSuccess = { jwtResponse ->
                    // Guardar token y usuario
                    _token.value = jwtResponse.token
                    _usuarioActual.value = jwtResponse.usuario

                    onResult(true, "Bienvenido ${jwtResponse.usuario.nombreUsuario}")
                },
                onFailure = { error ->
                    onResult(false, error.message ?: "Error al iniciar sesión")
                }
            )
        }
    }

    /**
     * Logout
     */
    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            _token.value?.let { token ->
                repository.logout(token)
            }
            // Limpiar estado
            _token.value = null
            _usuarioActual.value = null
            onComplete()
        }
    }

    /**
     * Guardar token en SharedPreferences (persistencia local)
     */
    fun guardarToken(context: Context, token: String) {
        val prefs = context.getSharedPreferences("oruga_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("jwt_token", token).apply()
        _token.value = token
    }

    /**
     * Cargar token guardado
     */
    fun cargarToken(context: Context): String? {
        val prefs = context.getSharedPreferences("oruga_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("jwt_token", null)
        _token.value = token
        return token
    }

    /**
     * Limpiar token guardado
     */
    fun limpiarToken(context: Context) {
        val prefs = context.getSharedPreferences("oruga_prefs", Context.MODE_PRIVATE)
        prefs.edit().remove("jwt_token").apply()
        _token.value = null
        _usuarioActual.value = null
    }

    /**
     * Verificar si está autenticado
     */
    fun estaAutenticado(): Boolean {
        return _token.value != null && _usuarioActual.value != null
    }

    /**
     * Verificar si es moderador
     */
    fun esModerador(): Boolean {
        return _usuarioActual.value?.esModerador == true
    }
}