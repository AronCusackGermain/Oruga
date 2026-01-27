package com.example.myapplication.data.remote.dtos

// ========== RESPONSE ==========
data class PublicacionDto(
    val id: Long,
    val autorId: Long,
    val autorNombre: String,
    val titulo: String,
    val contenido: String,
    val imagenUrl: String?,
    val fechaPublicacion: String,
    val fechaPublicacionFormateada: String,
    val cantidadLikes: Int,
    val cantidadComentarios: Int,
    val esAnuncio: Boolean
)