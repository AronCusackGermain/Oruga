package com.example.myapplication.data.remote.dto

data class EnviarMensajeGrupalRequest(
    val contenido: String,
    val imagenUrl: String = ""
)

data class EnviarMensajePrivadoRequest(
    val destinatarioId: Long,
    val contenido: String,
    val imagenUrl: String = ""
)

data class MensajeResponse(
    val id: Long,
    val remitenteId: Long,
    val remitenteNombre: String,
    val contenido: String,
    val imagenUrl: String,
    val fechaEnvio: String,
    val esGrupal: Boolean,
    val tipoMensaje: String
)
