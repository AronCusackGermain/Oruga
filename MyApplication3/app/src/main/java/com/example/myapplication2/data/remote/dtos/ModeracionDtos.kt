package com.example.myapplication.data.remote.dtos

// ========== REQUEST ==========
data class RevisarOrdenRequest(
    val aprobar: Boolean,
    val comentario: String
)

data class BanearUsuarioRequest(
    val razon: String
)

data class ReportarRequest(
    val tipoReporte: String, // "PUBLICACION", "MENSAJE", "USUARIO"
    val idContenido: Long,
    val razon: String,
    val descripcion: String
)