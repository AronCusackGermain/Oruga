package com.example.myapplication.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Modelo de datos para Comentario
 * Representa comentarios en publicaciones
 */
@Entity(tableName = "comentarios")
data class Comentario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val publicacionId: Int,
    val autorId: Int,
    val autorNombre: String,
    val contenido: String,
    val fechaComentario: Long = System.currentTimeMillis()
)