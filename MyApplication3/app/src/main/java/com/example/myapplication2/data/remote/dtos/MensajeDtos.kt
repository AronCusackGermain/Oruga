package com.example.myapplication.data.remote.dtos

// ========== REQUEST ==========
data class MensajePrivadoRequest(
    val destinatarioId: Long,
    val contenido: String
)

// ========== RESPONSE ==========
data class MensajeDto(
    val id: Long,
    val conversacionId: Long?,
    val remitenteId: Long,
    val remitenteNombre: String,
    val destinatarioId: Long?,
    val tipoMensaje: String,
    val contenido: String,
    val archivoUrl: String?,
    val fechaEnvio: String,
    val esGrupal: Boolean,
    val leido: Boolean
)