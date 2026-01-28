package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.dto.AuthResponse
import com.example.myapplication.data.remote.dto.UsuarioResponse
import com.example.myapplication.domain.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UsuarioViewModel(private val repository: UsuarioRepository) : ViewModel() {

    private val _authResponse = MutableStateFlow<AuthResponse?>(null)
    val authResponse: StateFlow<AuthResponse?> = _authResponse.asStateFlow()

    private val _usuarioActual = MutableStateFlow<UsuarioResponse?>(null)
    val usuarioActual: StateFlow<UsuarioResponse?> = _usuarioActual.asStateFlow()

    private val _todosLosUsuarios = MutableStateFlow<List<UsuarioResponse>>(emptyList())
    val todosLosUsuarios: StateFlow<List<UsuarioResponse>> = _todosLosUsuarios.asStateFlow()

    private val _usuariosConectados = MutableStateFlow<List<UsuarioResponse>>(emptyList())
    val usuariosConectados: StateFlow<List<UsuarioResponse>> = _usuariosConectados.asStateFlow()

    private val _usuariosDesconectados = MutableStateFlow<List<UsuarioResponse>>(emptyList())
    val usuariosDesconectados: StateFlow<List<UsuarioResponse>> = _usuariosDesconectados.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * Registrar
     */
    fun registrar(
        email: String,
        password: String,
        nombreUsuario: String,
        codigoModerador: String = "",
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true

            val result = repository.registrarUsuario(
                email, password, nombreUsuario, codigoModerador
            )

            result.fold(
                onSuccess = { authResponse ->
                    _authResponse.value = authResponse
                    cargarPerfilActual()
                    val mensaje = if (authResponse.esModerador) {
                        "¡Registro exitoso! Bienvenido Moderador ${authResponse.nombreUsuario}"
                    } else {
                        "Registro exitoso. Bienvenido ${authResponse.nombreUsuario}"
                    }
                    onResult(true, mensaje)
                },
                onFailure = { error ->
                    onResult(false, error.message ?: "Error al registrar")
                }
            )

            _isLoading.value = false
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
            _isLoading.value = true

            val result = repository.login(email, password)

            result.fold(
                onSuccess = { authResponse ->
                    _authResponse.value = authResponse
                    cargarPerfilActual()
                    onResult(true, "Bienvenido ${authResponse.nombreUsuario}")
                },
                onFailure = { error ->
                    onResult(false, error.message ?: "Error al iniciar sesión")
                }
            )

            _isLoading.value = false
        }
    }

    /**
     * Cargar perfil actual
     */
    private fun cargarPerfilActual() {
        viewModelScope.launch {
            val result = repository.obtenerPerfil()
            result.onSuccess { usuario ->
                _usuarioActual.value = usuario
            }
        }
    }

    /**
     * Cargar todos los usuarios
     */
    fun cargarUsuarios() {
        viewModelScope.launch {
            val result = repository.obtenerUsuarios()
            result.onSuccess { usuarios ->
                _todosLosUsuarios.value = usuarios
                _usuariosConectados.value = usuarios.filter { it.estadoConexion }
                _usuariosDesconectados.value = usuarios.filter { !it.estadoConexion }
            }
        }
    }

    /**
     * Actualizar perfil
     */
    fun actualizarPerfil(
        descripcion: String?,
        urlFotoPerfil: String?,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.actualizarPerfil(descripcion, urlFotoPerfil)
            result.fold(
                onSuccess = { usuario ->
                    _usuarioActual.value = usuario
                    onResult(true, "Perfil actualizado")
                },
                onFailure = { error ->
                    onResult(false, error.message ?: "Error al actualizar")
                }
            )
        }
    }

    /**
     * Conectar cuentas externas
     */
    fun conectarCuentasExternas(
        steamId: String,
        discordId: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.conectarCuentasExternas(
                if (steamId.isNotBlank()) steamId else null,
                if (discordId.isNotBlank()) discordId else null
            )
            result.fold(
                onSuccess = { usuario ->
                    _usuarioActual.value = usuario
                    onResult(true, "Cuentas conectadas")
                },
                onFailure = { error ->
                    onResult(false, error.message ?: "Error")
                }
            )
        }
    }

    /**
     * Logout
     */
    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.logout()
            _authResponse.value = null
            _usuarioActual.value = null
            onComplete()
        }
    }

    /**
     * Obtener token
     */
    fun getToken(): String {
        return repository.getToken()
    }
}

/**
 * Factory para UsuarioViewModel
 */
class UsuarioViewModelFactory(
    private val repository: UsuarioRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsuarioViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UsuarioViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
