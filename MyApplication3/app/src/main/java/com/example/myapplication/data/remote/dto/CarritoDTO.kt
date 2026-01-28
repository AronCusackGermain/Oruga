package com.example.myapplication.data.remote.dto

data class AgregarCarritoRequest(
    val juegoId: Int,
    val nombreJuego: String,
    val precioUnitario: Double,
    val cantidad: Int = 1,
    val imagenUrl: Int
)

data class ActualizarCantidadRequest(
    val cantidad: Int
)

data class ItemCarritoResponse(
    val id: Long,
    val juegoId: Long,
    val nombreJuego: String,
    val precioUnitario: Double,
    val cantidad: Int,
    val imagenUrl: String,
    val subtotal: Double,
    val fechaAgregado: String
)

data class CompraResponse(
    val mensaje: String,
    val total: Double,
    val cantidadItems: Int
)