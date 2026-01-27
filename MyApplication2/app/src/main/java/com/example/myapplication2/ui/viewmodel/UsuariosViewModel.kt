package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.models.Usuario
import com.example.myapplication.domain.repository.OrugaRepository
import com.example.myapplication.domain.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para gestionar el estado y lÃ³gica de Usuario
 */
class UsuarioViewModel(private val repository: UsuarioRepository) : ViewModel() {

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    private val _todosLosUsuarios = MutableStateFlow<List<Usuario>>(emptyList())
    val todosLosUsuarios: StateFlow<List<Usuario>> = _todosLosUsuarios.asStateFlow()

    private val _usuariosConectados = MutableStateFlow<List<Usuario>>(emptyList())
    val usuariosConectados: StateFlow<List<Usuario>> = _usuariosConectados.asStateFlow()

    private val _usuariosDesconectados = MutableStateFlow<List<Usuario>>(emptyList())
    val usuariosDesconectados: StateFlow<List<Usuario>> = _usuariosDesconectados.asStateFlow()

    private val orugaRepository = OrugaRepository()

    init {
        cargarTodosLosUsuarios()
        cargarUsuariosPorEstado()
    }

    /**
     * Registra un nuevo usuario
     */
    fun registrar(
        email: String,
        password: String,
        nombreUsuario: String,
        codigoModerador: String = "",
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.registrarUsuario(email, password, nombreUsuario, codigoModerador)
            result.fold(
                onSuccess = { id ->
                    // Cargar el usuario reciÃ©n creado
                    val usuario = repository.obtenerUsuarioPorId(id.toInt())
                    _usuarioActual.value = usuario

                    // Mensaje personalizado segÃºn si es moderador
                    val mensaje = if (usuario?.esModerador == true) {
                        "Â¡Registro exitoso! Bienvenido Moderador ${usuario.nombreUsuario} ðŸ›¡ï¸"
                    } else {
                        "Registro exitoso. Bienvenido ${usuario?.nombreUsuario ?: ""}!"
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
     * Inicia sesiÃ³n
     */
    fun login(email: String, password: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.login(email, password)
            result.fold(
                onSuccess = { usuario ->
                    _usuarioActual.value = usuario
                    onResult(true, "Bienvenido ${usuario.nombreUsuario}")
                },
                onFailure = { error ->
                    onResult(false, error.message ?: "Error al iniciar sesiÃ³n")
                }
            )
        }
    }

    /**
     * Cierra sesiÃ³n
     */
    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            _usuarioActual.value?.let { usuario ->
                repository.logout(usuario.id)
            }
            _usuarioActual.value = null
            onComplete()
        }
    }

    /**
     * Carga todos los usuarios
     */
    private fun cargarTodosLosUsuarios() {
        viewModelScope.launch {
            repository.obtenerTodosLosUsuarios().collect { usuarios ->
                _todosLosUsuarios.value = usuarios
            }
        }
    }

    /**
     * Carga usuarios por estado de conexiÃ³n
     */
    private fun cargarUsuariosPorEstado() {
        viewModelScope.launch {
            // Usuarios conectados
            repository.obtenerUsuariosPorEstado(true).collect { usuarios ->
                _usuariosConectados.value = usuarios
            }
        }

        viewModelScope.launch {
            // Usuarios desconectados
            repository.obtenerUsuariosPorEstado(false).collect { usuarios ->
                _usuariosDesconectados.value = usuarios
            }
        }
    }

    /**
     * Actualiza el perfil del usuario
     */
    fun actualizarPerfil(
        descripcion: String,
        urlFotoPerfil: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            _usuarioActual.value?.let { usuario ->
                val usuarioActualizado = usuario.copy(
                    descripcion = descripcion,
                    urlFotoPerfil = urlFotoPerfil
                )

                val result = repository.actualizarUsuario(usuarioActualizado)
                result.fold(
                    onSuccess = {
                        _usuarioActual.value = usuarioActualizado
                        onResult(true, "Perfil actualizado")
                    },
                    onFailure = { error ->
                        onResult(false, error.message ?: "Error al actualizar")
                    }
                )
            }
        }
    }

    /**
     * Conecta cuentas externas (Steam, Discord)
     */
    fun conectarCuentasExternas(
        steamId: String,
        discordId: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            _usuarioActual.value?.let { usuario ->
                val result = repository.actualizarConexionesExternas(
                    usuario.id,
                    steamId,
                    discordId
                )

                result.fold(
                    onSuccess = {
                        // Actualizar usuario actual
                        val usuarioActualizado = usuario.copy(
                            steamId = steamId,
                            discordId = discordId
                        )
                        _usuarioActual.value = usuarioActualizado
                        onResult(true, "Cuentas conectadas")
                    },
                    onFailure = { error ->
                        onResult(false, error.message ?: "Error al conectar cuentas")
                    }
                )
            }
        }
    }

}
