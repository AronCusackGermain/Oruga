package com.example.myapplication.data.remote.dto

data class CrearPublicacionRequest(
    val titulo: String,
    val contenido: String,
    val imagenesUrls: String = "",
    val esAnuncio: Boolean = false
)

data class PublicacionResponse(
    val id: Long,
    val autorNombre: String,
    val titulo: String,
    val contenido: String,
    val imagenUrl: String,
    val imagenesUrls: String,
    val fechaPublicacion: String,
    val cantidadLikes: Int,
    val cantidadComentarios: Int,
    val esAnuncio: Boolean
)