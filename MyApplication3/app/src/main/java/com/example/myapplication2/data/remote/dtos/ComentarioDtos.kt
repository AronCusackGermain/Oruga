package com.example.myapplication.data.remote.dtos

// ========== REQUEST ==========
data class ComentarioRequest(
    val contenido: String
)

// ========== RESPONSE ==========
data class ComentarioDto(
    val id: Long,
    val publicacionId: Long?,
    val autorId: Long,
    val autorNombre: String,
    val contenido: String,
    val fechaComentario: String
)