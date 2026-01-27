package com.example.myapplication.domain.repository

import com.example.myapplication.data.dao.CarritoDao
import com.example.myapplication.data.models.ItemCarrito
import kotlinx.coroutines.flow.Flow

/**
 * Repositorio para gestionar el carrito de compras
 */
class CarritoRepository(private val carritoDao: CarritoDao) {

    /**
     * Agregar o actualizar item en el carrito
     */
    suspend fun agregarAlCarrito(
        usuarioId: Int,
        juegoId: Int,
        nombreJuego: String,
        precio: Double,
        imagenUrl: Int,
        cantidad: Int = 1
    ): Result<Long> {
        return try {
            // Verificar si el juego ya está en el carrito
            val itemExistente = carritoDao.buscarItemEnCarrito(usuarioId, juegoId)

            if (itemExistente != null) {
                // Si existe, actualizar cantidad
                val nuevaCantidad = itemExistente.cantidad + cantidad
                carritoDao.actualizarCantidad(itemExistente.id, nuevaCantidad)
                Result.success(itemExistente.id.toLong())
            } else {
                // Si no existe, crear nuevo item
                val nuevoItem = ItemCarrito(
                    usuarioId = usuarioId,
                    juegoId = juegoId,
                    nombreJuego = nombreJuego,
                    precioUnitario = precio,
                    cantidad = cantidad,
                    imagenUrl = imagenUrl
                )
                val id = carritoDao.agregarAlCarrito(nuevoItem)
                Result.success(id)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtener carrito del usuario
     */
    fun obtenerCarrito(usuarioId: Int): Flow<List<ItemCarrito>> {
        return carritoDao.obtenerCarritoPorUsuario(usuarioId)
    }

    /**
     * Actualizar cantidad de un item
     */
    suspend fun actualizarCantidad(itemId: Int, cantidad: Int): Result<Unit> {
        return try {
            if (cantidad <= 0) {
                return Result.failure(Exception("La cantidad debe ser mayor a 0"))
            }
            carritoDao.actualizarCantidad(itemId, cantidad)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Eliminar item del carrito
     */
    suspend fun eliminarDelCarrito(item: ItemCarrito): Result<Unit> {
        return try {
            carritoDao.eliminarDelCarrito(item)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Vaciar todo el carrito
     */
    suspend fun vaciarCarrito(usuarioId: Int): Result<Unit> {
        return try {
            carritoDao.vaciarCarrito(usuarioId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Contar items en carrito
     */
    fun contarItems(usuarioId: Int): Flow<Int> {
        return carritoDao.contarItemsEnCarrito(usuarioId)
    }

    /**
     * Calcular total del carrito
     */
    suspend fun calcularTotal(items: List<ItemCarrito>): Double {
        return items.sumOf { it.calcularSubtotal() }
    }
}