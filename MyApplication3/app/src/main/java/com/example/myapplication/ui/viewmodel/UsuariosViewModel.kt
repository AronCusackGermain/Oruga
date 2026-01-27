package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.dto.AuthResponse
import com.example.myapplication.domain.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UsuarioViewModel(private val repository: UsuarioRepository) : ViewModel() {

    private val _authResponse = MutableStateFlow<AuthResponse?>(null)
    val authResponse: StateFlow<AuthResponse?> = _authResponse.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * Registrar con API
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
     * Login con API
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
     * Logout
     */
    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.logout()
            _authResponse.value = null
            onComplete()
        }
    }

    /**
     * Obtener token para peticiones
     */
    fun getToken(): String {
        return repository.getToken()
    }
}