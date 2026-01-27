package com.example.myapplication.data.models


import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Modelo de datos para Usuario
 * Representa a un usuario del foro Oruga
 */
/*@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val email: String,
    val password: String,
    val nombreUsuario: String,
    val esModerador: Boolean = false,
    val urlFotoPerfil: String = "",
    val descripcion: String = "",
    val steamId: String = "",
    val discordId: String = "",
    val fechaCreacion: Long = System.currentTimeMillis(),
    val estadoConexion: Boolean = false // true = conectado, false = desconectado
)*/

/**
 * Modelo de datos para Usuario
 * Representa a un usuario del foro Oruga con roles diferenciados
 */
@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val email: String,
    val password: String,
    val nombreUsuario: String,

    // Roles y permisos
    val esModerador: Boolean = false,
    val estaBaneado: Boolean = false,
    val fechaBaneo: Long? = null,
    val razonBaneo: String = "",

    // Perfil
    val urlFotoPerfil: String = "",
    val descripcion: String = "",

    // Conexiones externas
    val steamId: String = "",
    val discordId: String = "",

    // Metadata
    val fechaCreacion: Long = System.currentTimeMillis(),
    val ultimaConexion: Long = System.currentTimeMillis(),
    val estadoConexion: Boolean = false,

    // EstadÃ­sticas (para moderadores)
    val cantidadPublicaciones: Int = 0,
    val cantidadMensajes: Int = 0,
    val cantidadReportes: Int = 0
)

/**
 * Enum para roles de usuario
 */
enum class RolUsuario {
    USUARIO,
    MODERADOR,
    ADMIN // Para futuras expansiones
}

/**
 * Extension para obtener el rol
 */
fun Usuario.obtenerRol(): RolUsuario {
    return if (esModerador) RolUsuario.MODERADOR else RolUsuario.USUARIO
}

/**
 * Extension para verificar si puede realizar acciones de moderador
 */
fun Usuario.puedeModerar(): Boolean {
    return esModerador && !estaBaneado
}
