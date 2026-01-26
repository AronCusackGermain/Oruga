package com.example.myapplication.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * PUBLICACIÓN ACTUALIZADA - Ahora soporta múltiples imágenes
 */
@Entity(tableName = "publicaciones")
data class Publicacion(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val autorId: Int,
    val autorNombre: String,
    val titulo: String,
    val contenido: String,
    val imagenUrl: String = "", // URL de imagen principal
    val imagenesUrls: String = "", // Lista de URLs separadas por comas
    val fechaPublicacion: Long = System.currentTimeMillis(),
    val cantidadLikes: Int = 0,
    val cantidadComentarios: Int = 0,
    val esAnuncio: Boolean = false
)

/**
 * MENSAJE ACTUALIZADO - Ahora soporta imágenes
 */
