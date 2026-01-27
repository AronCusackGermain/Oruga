package com.example.myapplication.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mensajes")
data class Mensaje(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val remitenteId: Int,
    val remitenteNombre: String,
    val destinatarioId: Int? = null,
    val contenido: String,
    val imagenUrl: String = "", // URL de imagen adjunta
    val fechaEnvio: Long = System.currentTimeMillis(),
    val esGrupal: Boolean = true,
    val leido: Boolean = false,
    val tipoMensaje: TipoMensaje = TipoMensaje.TEXTO
)

/**
 * Tipos de mensaje
 */
enum class TipoMensaje {
    TEXTO,
    IMAGEN,
    TEXTO_CON_IMAGEN
}

/**
 * Extension para verificar si tiene imagen
 */
fun Mensaje.tieneImagen(): Boolean = imagenUrl.isNotEmpty()

/*
/**
 * Modelo de datos para Mensaje
 * Representa mensajes en chats (grupal o privado)
 */
@Entity(tableName = "mensajes")
data class Mensaje(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val remitenteId: Int,
    val remitenteNombre: String,
    val destinatarioId: Int? = null, // null para chat grupal
    val contenido: String,
    val fechaEnvio: Long = System.currentTimeMillis(),
    val esGrupal: Boolean = true,
    val leido: Boolean = false
)*/
