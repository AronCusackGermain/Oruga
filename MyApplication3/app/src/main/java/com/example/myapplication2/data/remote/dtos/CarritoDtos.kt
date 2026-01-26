package com.example.myapplication.data.remote.dtos

// ========== REQUEST ==========
data class AgregarCarritoRequest(
    val juegoId: Long,
    val cantidad: Int
)

// ========== RESPONSE ==========
data class CarritoResponseDto(
    val id: Long,
    val items: List<CarritoItemDto>,
    val cantidadItems: Int,
    val subtotal: Double,
    val subtotalFormateado: String,
    val descuento: Double,
    val descuentoFormateado: String,
    val total: Double,
    val totalFormateado: String,
    val tieneDescuentoModerador: Boolean
)

data class CarritoItemDto(
    val id: Long,
    val juegoId: Long,
    val nombre: String,
    val descripcion: String?,
    val imagenUrl: String?,
    val precioUnitario: Double,
    val precioUnitarioFormateado: String,
    val cantidad: Int,
    val stockDisponible: Int,
    val subtotal: Double,
    val subtotalFormateado: String
)