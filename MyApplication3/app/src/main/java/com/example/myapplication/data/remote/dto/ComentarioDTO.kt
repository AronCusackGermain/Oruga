package com.example.myapplication.data.remote.dto

data class CrearComentarioRequest(
    val publicacionId: Long,
    val contenido: String
)

data class ComentarioResponse(
    val id: Long,
    val autorNombre: String,
    val contenido: String,
    val fechaComentario: String
)