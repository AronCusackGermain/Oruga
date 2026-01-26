package com.example.myapplication.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Modelo de datos para Item en Carrito
 * Representa juegos agregados al carrito de compras
 */
@Entity(tableName = "carrito")
data class ItemCarrito(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val usuarioId: Int,
    val juegoId: Int,
    val nombreJuego: String,
    val precioUnitario: Double, // En CLP
    val cantidad: Int = 1,
    val imagenUrl: Int,
    val fechaAgregado: Long = System.currentTimeMillis()
) {
    // Calcular subtotal
    fun calcularSubtotal(): Double = precioUnitario * cantidad
}