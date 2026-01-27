package com.example.myapplication.data.dao

import androidx.room.*
import com.example.myapplication.data.models.Usuario
import kotlinx.coroutines.flow.Flow


/**
 * DAO para operaciones con Usuario en la base de datos
 */
@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarUsuario(usuario: Usuario): Long

    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun buscarPorEmail(email: String): Usuario?

    @Query("SELECT EXISTS(SELECT 1 FROM usuarios WHERE email = :email)")
    suspend fun existeEmail(email: String): Boolean

    @Query("SELECT * FROM usuarios WHERE id = :id")
    suspend fun buscarPorId(id: Int): Usuario?

    @Query("SELECT * FROM usuarios ORDER BY nombreUsuario ASC")
    fun obtenerTodosLosUsuarios(): Flow<List<Usuario>>

    @Query("SELECT * FROM usuarios WHERE estadoConexion = :conectado ORDER BY nombreUsuario ASC")
    fun obtenerUsuariosPorEstado(conectado: Boolean): Flow<List<Usuario>>

    @Update
    suspend fun actualizarUsuario(usuario: Usuario)

    @Query("UPDATE usuarios SET estadoConexion = :estado WHERE id = :usuarioId")
    suspend fun actualizarEstadoConexion(usuarioId: Int, estado: Boolean)

    @Query("UPDATE usuarios SET steamId = :steamId, discordId = :discordId WHERE id = :usuarioId")
    suspend fun actualizarConexionesExternas(usuarioId: Int, steamId: String, discordId: String)

    @Delete
    suspend fun eliminarUsuario(usuario: Usuario)

    // NUEVAS FUNCIONES PARA MODERADORES

    @Query("UPDATE usuarios SET estaBaneado = :baneado, fechaBaneo = :fecha, razonBaneo = :razon WHERE id = :usuarioId")
    suspend fun banearUsuario(usuarioId: Int, baneado: Boolean, fecha: Long?, razon: String)

    @Query("SELECT * FROM usuarios WHERE estaBaneado = 1 ORDER BY fechaBaneo DESC")
    fun obtenerUsuariosBaneados(): Flow<List<Usuario>>

    @Query("UPDATE usuarios SET cantidadPublicaciones = cantidadPublicaciones + 1 WHERE id = :usuarioId")
    suspend fun incrementarPublicaciones(usuarioId: Int)

    @Query("UPDATE usuarios SET cantidadMensajes = cantidadMensajes + 1 WHERE id = :usuarioId")
    suspend fun incrementarMensajes(usuarioId: Int)

    @Query("UPDATE usuarios SET cantidadReportes = cantidadReportes + 1 WHERE id = :usuarioId")
    suspend fun incrementarReportes(usuarioId: Int)

    @Query("UPDATE usuarios SET ultimaConexion = :timestamp WHERE id = :usuarioId")
    suspend fun actualizarUltimaConexion(usuarioId: Int, timestamp: Long)
}

/**
 * DAO para operaciones con Usuario en la base de datos
 */
/*@Dao
interface UsuarioDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarUsuario(usuario: Usuario): Long

    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun buscarPorEmail(email: String): Usuario?

    @Query("SELECT EXISTS(SELECT 1 FROM usuarios WHERE email = :email)")
    suspend fun existeEmail(email: String): Boolean

    @Query("SELECT * FROM usuarios WHERE id = :id")
    suspend fun buscarPorId(id: Int): Usuario?

    @Query("SELECT * FROM usuarios ORDER BY nombreUsuario ASC")
    fun obtenerTodosLosUsuarios(): Flow<List<Usuario>>

    @Query("SELECT * FROM usuarios WHERE estadoConexion = :conectado ORDER BY nombreUsuario ASC")
    fun obtenerUsuariosPorEstado(conectado: Boolean): Flow<List<Usuario>>

    @Update
    suspend fun actualizarUsuario(usuario: Usuario)

    @Query("UPDATE usuarios SET estadoConexion = :estado WHERE id = :usuarioId")
    suspend fun actualizarEstadoConexion(usuarioId: Int, estado: Boolean)

    @Query("UPDATE usuarios SET steamId = :steamId, discordId = :discordId WHERE id = :usuarioId")
    suspend fun actualizarConexionesExternas(usuarioId: Int, steamId: String, discordId: String)

    @Delete
    suspend fun eliminarUsuario(usuario: Usuario)
}*/
