package com.example.myapplication.data.remote.dtos

// ========== RESPONSE ==========
data class OrdenDto(
    val id: Long,
    val numeroOrden: String,
    val usuarioId: Long,
    val items: String, // JSON string
    val subtotal: Double,
    val subtotalFormateado: String,
    val descuento: Double,
    val descuentoFormateado: String,
    val total: Double,
    val totalFormateado: String,
    val estado: String,
    val estadoDescripcion: String,
    val metodoPago: String,
    val comprobanteUrl: String?,
    val fechaCreacion: String,
    val fechaCreacionFormateada: String,
    val fechaRevision: String?,
    val comentarioModerador: String?
)

data class CheckoutResponseDto(
    val orden: OrdenDto,
    val datosBancarios: DatosBancariosDto
)

data class DatosBancariosDto(
    val nombreTitular: String,
    val banco: String,
    val tipoCuenta: String,
    val numeroCuenta: String,
    val rutTitular: String,
    val emailConfirmacion: String
)