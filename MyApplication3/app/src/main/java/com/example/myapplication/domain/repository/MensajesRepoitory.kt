package com.example.myapplication.domain.repository

import com.example.myapplication.data.dao.MensajeDao
import com.example.myapplication.data.models.Mensaje
import com.example.myapplication.data.models.TipoMensaje
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio para gestionar operaciones relacionadas con Mensaje
 * ACTUALIZADO: Soporte para imÃ¡genes
 */
class MensajeRepository(private val mensajeDao: MensajeDao) {

    /**
     * EnvÃ­a un mensaje grupal (con o sin imagen)
     */
    suspend fun enviarMensajeGrupal(
        remitenteId: Int,
        remitenteNombre: String,
        contenido: String,
        imagenPath: String = "" // NUEVO: Ruta de imagen opcional
    ): Result<Long> {
        return try {
            if (contenido.isBlank() && imagenPath.isBlank()) {
                return Result.failure(Exception("Debes enviar un mensaje o una imagen"))
            }

            val tipoMensaje = when {
                contenido.isNotBlank() && imagenPath.isNotBlank() -> TipoMensaje.TEXTO_CON_IMAGEN
                imagenPath.isNotBlank() -> TipoMensaje.IMAGEN
                else -> TipoMensaje.TEXTO
            }

            val mensaje = Mensaje(
                remitenteId = remitenteId,
                remitenteNombre = remitenteNombre,
                contenido = contenido,
                imagenUrl = imagenPath,
                esGrupal = true,
                tipoMensaje = tipoMensaje
            )

            val id = mensajeDao.insertarMensaje(mensaje)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * EnvÃ­a un mensaje privado (con o sin imagen)
     */
    suspend fun enviarMensajePrivado(
        remitenteId: Int,
        remitenteNombre: String,
        destinatarioId: Int,
        contenido: String,
        imagenPath: String = "" // NUEVO: Ruta de imagen opcional
    ): Result<Long> {
        return try {
            if (contenido.isBlank() && imagenPath.isBlank()) {
                return Result.failure(Exception("Debes enviar un mensaje o una imagen"))
            }

            val tipoMensaje = when {
                contenido.isNotBlank() && imagenPath.isNotBlank() -> TipoMensaje.TEXTO_CON_IMAGEN
                imagenPath.isNotBlank() -> TipoMensaje.IMAGEN
                else -> TipoMensaje.TEXTO
            }

            val mensaje = Mensaje(
                remitenteId = remitenteId,
                remitenteNombre = remitenteNombre,
                destinatarioId = destinatarioId,
                contenido = contenido,
                imagenUrl = imagenPath,
                esGrupal = false,
                tipoMensaje = tipoMensaje
            )

            val id = mensajeDao.insertarMensaje(mensaje)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene mensajes del chat grupal
     */
    fun obtenerMensajesGrupales(): Flow<List<Mensaje>> {
        return mensajeDao.obtenerMensajesGrupales()
    }

    /**
     * Obtiene mensajes privados entre dos usuarios
     */
    fun obtenerMensajesPrivados(usuarioId: Int, otroUsuarioId: Int): Flow<List<Mensaje>> {
        return mensajeDao.obtenerMensajesPrivados(usuarioId, otroUsuarioId)
    }

    /**
     * Obtiene la lista de conversaciones del usuario
     */
    suspend fun obtenerConversaciones(usuarioId: Int): List<Int> {
        return try {
            mensajeDao.obtenerConversaciones(usuarioId)
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Marca mensajes como leÃ­dos
     */
    suspend fun marcarComoLeidos(usuarioId: Int): Result<Unit> {
        return try {
            mensajeDao.marcarMensajesComoLeidos(usuarioId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Cuenta mensajes no leÃ­dos
     */
    fun contarMensajesNoLeidos(usuarioId: Int): Flow<Int> {
        return mensajeDao.contarMensajesNoLeidos(usuarioId)
    }
}
