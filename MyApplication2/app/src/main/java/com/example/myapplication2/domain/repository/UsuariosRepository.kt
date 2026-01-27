package com.example.myapplication.domain.repository

import com.example.myapplication.data.dao.UsuarioDao
import com.example.myapplication.data.models.Usuario
import com.example.myapplication.data.config.ModeradorConfig
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio para gestionar operaciones relacionadas con Usuario
 * Implementa la lÃ³gica de negocio y actÃºa como puente entre DAO y ViewModel
 */
class UsuarioRepository(private val usuarioDao: UsuarioDao) {

    /**
     * Registra un nuevo usuario
     * Valida que el email no exista previamente
     * Determina automÃ¡ticamente si debe ser moderador
     */
    suspend fun registrarUsuario(
        email: String,
        password: String,
        nombreUsuario: String,
        codigoModerador: String = ""
    ): Result<Long> {
        return try {
            // Validar que el email no estÃ© vacÃ­o
            if (email.isBlank() || password.isBlank() || nombreUsuario.isBlank()) {
                return Result.failure(Exception("Todos los campos son obligatorios"))
            }

            // Validar formato de email
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                return Result.failure(Exception("Email invÃ¡lido"))
            }

            // Validar que la contraseÃ±a tenga al menos 6 caracteres
            if (password.length < 6) {
                return Result.failure(Exception("La contraseÃ±a debe tener al menos 6 caracteres"))
            }

            // Verificar si el email ya existe
            if (usuarioDao.existeEmail(email)) {
                return Result.failure(Exception("El email ya estÃ¡ registrado"))
            }

            // DETERMINAR SI ES MODERADOR
            var esModerador = false
            var mensajeAdicional = ""

            // OpciÃ³n 1: Verificar si el email estÃ¡ en la lista predefinida
            if (ModeradorConfig.esModeradorAutomatico(email)) {
                esModerador = true
                mensajeAdicional = " Â¡Bienvenido Moderador!"
            }
            // OpciÃ³n 2: Verificar si ingresÃ³ un cÃ³digo vÃ¡lido
            else if (codigoModerador.isNotBlank() && ModeradorConfig.validarCodigoModerador(codigoModerador)) {
                esModerador = true
                mensajeAdicional = " Â¡CÃ³digo de moderador vÃ¡lido!"
            }

            val usuario = Usuario(
                email = email,
                password = password,
                nombreUsuario = nombreUsuario,
                esModerador = esModerador
            )

            val id = usuarioDao.insertarUsuario(usuario)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Autentica un usuario
     * Verifica email y contraseÃ±a
     * Control exhaustivo de excepciones
     */
    suspend fun login(email: String, password: String): Result<Usuario> {
        return try {
            // ValidaciÃ³n 1: Campos vacÃ­os
            if (email.isBlank()) {
                return Result.failure(Exception("El email es obligatorio"))
            }

            if (password.isBlank()) {
                return Result.failure(Exception("La contraseÃ±a es obligatoria"))
            }

            // ValidaciÃ³n 2: Formato de email
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                return Result.failure(Exception("El formato del email no es vÃ¡lido"))
            }

            // ValidaciÃ³n 3: Buscar usuario
            val usuario = usuarioDao.buscarPorEmail(email)

            if (usuario == null) {
                return Result.failure(Exception("No existe una cuenta con este email"))
            }

            // ValidaciÃ³n 4: Usuario baneado
            if (usuario.estaBaneado) {
                val razon = if (usuario.razonBaneo.isNotBlank()) {
                    "Tu cuenta ha sido suspendida. RazÃ³n: ${usuario.razonBaneo}"
                } else {
                    "Tu cuenta ha sido suspendida. Contacta al administrador."
                }
                return Result.failure(Exception(razon))
            }

            // ValidaciÃ³n 5: Verificar contraseÃ±a
            if (usuario.password != password) {
                return Result.failure(Exception("La contraseÃ±a es incorrecta"))
            }

            // Login exitoso: actualizar estado de conexiÃ³n
            usuarioDao.actualizarEstadoConexion(usuario.id, true)
            usuarioDao.actualizarUltimaConexion(usuario.id, System.currentTimeMillis())

            Result.success(usuario)

        } catch (e: Exception) {
            // Capturar cualquier error inesperado
            Result.failure(
                Exception("Error al iniciar sesiÃ³n: ${e.message ?: "Error desconocido"}")
            )
        }
    }

    /**
     * Cierra sesiÃ³n del usuario
     */
    suspend fun logout(usuarioId: Int) {
        try {
            usuarioDao.actualizarEstadoConexion(usuarioId, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Obtiene un usuario por su ID
     */
    suspend fun obtenerUsuarioPorId(id: Int): Usuario? {
        return try {
            usuarioDao.buscarPorId(id)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Obtiene todos los usuarios
     */
    fun obtenerTodosLosUsuarios(): Flow<List<Usuario>> {
        return usuarioDao.obtenerTodosLosUsuarios()
    }

    /**
     * Obtiene usuarios por estado de conexiÃ³n
     */
    fun obtenerUsuariosPorEstado(conectado: Boolean): Flow<List<Usuario>> {
        return usuarioDao.obtenerUsuariosPorEstado(conectado)
    }

    /**
     * Actualiza los datos del usuario
     */
    suspend fun actualizarUsuario(usuario: Usuario): Result<Unit> {
        return try {
            usuarioDao.actualizarUsuario(usuario)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualiza las conexiones externas del usuario (Steam, Discord)
     */
    suspend fun actualizarConexionesExternas(
        usuarioId: Int,
        steamId: String,
        discordId: String
    ): Result<Unit> {
        return try {
            usuarioDao.actualizarConexionesExternas(usuarioId, steamId, discordId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
