package com.example.myapplication.data.dao

import androidx.room.*
import com.example.myapplication.data.models.Mensaje
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operaciones con Mensaje en la base de datos
 */
@Dao
interface MensajeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarMensaje(mensaje: Mensaje): Long

    @Query("SELECT * FROM mensajes WHERE esGrupal = 1 ORDER BY fechaEnvio ASC")
    fun obtenerMensajesGrupales(): Flow<List<Mensaje>>

    @Query("""
        SELECT * FROM mensajes 
        WHERE esGrupal = 0 
        AND ((remitenteId = :usuarioId AND destinatarioId = :otroUsuarioId) 
        OR (remitenteId = :otroUsuarioId AND destinatarioId = :usuarioId))
        ORDER BY fechaEnvio ASC
    """)
    fun obtenerMensajesPrivados(usuarioId: Int, otroUsuarioId: Int): Flow<List<Mensaje>>

    @Query("""
        SELECT DISTINCT 
        CASE 
            WHEN remitenteId = :usuarioId THEN destinatarioId 
            ELSE remitenteId 
        END as usuarioId
        FROM mensajes 
        WHERE esGrupal = 0 
        AND (remitenteId = :usuarioId OR destinatarioId = :usuarioId)
    """)
    suspend fun obtenerConversaciones(usuarioId: Int): List<Int>

    @Query("UPDATE mensajes SET leido = 1 WHERE destinatarioId = :usuarioId AND leido = 0")
    suspend fun marcarMensajesComoLeidos(usuarioId: Int)

    @Query("SELECT COUNT(*) FROM mensajes WHERE destinatarioId = :usuarioId AND leido = 0")
    fun contarMensajesNoLeidos(usuarioId: Int): Flow<Int>

    @Delete
    suspend fun eliminarMensaje(mensaje: Mensaje)
}
