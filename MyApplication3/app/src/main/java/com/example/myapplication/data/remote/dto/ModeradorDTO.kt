package com.example.myapplication.data.remote.dto

// ========== MODERACIÓN ==========
data class BanearUsuarioRequest(
    val razon: String,
    val duracionDias: Int? = null // null = permanente
)

data class EliminarContenidoRequest(
    val razon: String
)

// ========== REPORTES ==========
data class CrearReporteRequest(
    val tipoReporte: String, // "USUARIO", "PUBLICACION", "COMENTARIO", "MENSAJE"
    val idContenido: Long,
    val razon: String,
    val descripcion: String
)

data class ReporteResponse(
    val id: Long,
    val reportadoPorId: Long,
    val reportadoPorNombre: String,
    val tipoReporte: String,
    val idContenido: Long,
    val razon: String,
    val descripcion: String,
    val estado: String, // "PENDIENTE", "RESUELTO", "RECHAZADO"
    val fechaReporte: String,
    val moderadorId: Long?,
    val moderadorNombre: String?,
    val fechaResolucion: String?,
    val accionTomada: String?
)

data class ResolverReporteRequest(
    val aceptado: Boolean,
    val accionTomada: String
)

data class EstadisticasResponse(
    val totalUsuarios: Int,
    val usuariosActivos: Int,
    val totalPublicaciones: Int,
    val totalMensajes: Int,
    val reportesPendientes: Int,
    val usuariosBaneados: Int
)