package com.example.myapplication.domain.repository

import com.example.myapplication.data.remote.RetrofitClient
import com.example.myapplication.data.remote.dto.AgregarCarritoRequest
import com.example.myapplication.data.remote.dto.ActualizarCantidadRequest
import com.example.myapplication.data.remote.dto.ItemCarritoResponse
import com.example.myapplication.data.remote.dto.CompraResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repositorio para gestionar el carrito con API REST
 */
class CarritoRepository {

    private val apiService = RetrofitClient.apiService

    /**
     * Agregar al carrito
     */
    suspend fun agregarAlCarrito(
        juegoId: Int,
        nombreJuego: String,
        precio: Double,
        imagenUrl: Int,
        cantidad: Int = 1
    ): Result<ItemCarritoResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            val token = RetrofitClient.getAuthToken() ?: ""

            val request = AgregarCarritoRequest(
                juegoId = juegoId,
                nombreJuego = nombreJuego,
                precioUnitario = precio,
                cantidad = cantidad,
                imagenUrl = imagenUrl
            )

            val response = apiService.agregarAlCarrito("Bearer $token", request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al agregar al carrito"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error de conexión: ${e.message}"))
        }
    }

    /**
     * Obtener carrito
     */
    suspend fun obtenerCarrito(): Result<List<ItemCarritoResponse>> =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val token = RetrofitClient.getAuthToken() ?: ""
                val response = apiService.obtenerCarrito("Bearer $token")

                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error al obtener carrito"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    /**
     * Actualizar cantidad
     */
    suspend fun actualizarCantidad(
        itemId: Long,
        cantidad: Int
    ): Result<ItemCarritoResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            val token = RetrofitClient.getAuthToken() ?: ""
            val request = ActualizarCantidadRequest(cantidad)
            val response = apiService.actualizarCantidad("Bearer $token", itemId, request)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al actualizar cantidad"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Eliminar item
     */
    suspend fun eliminarItem(itemId: Long): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val token = RetrofitClient.getAuthToken() ?: ""
            val response = apiService.eliminarItem("Bearer $token", itemId)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al eliminar item"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Vaciar carrito
     */
    suspend fun vaciarCarrito(): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val token = RetrofitClient.getAuthToken() ?: ""
            val response = apiService.vaciarCarrito("Bearer $token")

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al vaciar carrito"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Contar items
     */
    suspend fun contarItems(): Result<Long> = withContext(Dispatchers.IO) {
        return@withContext try {
            val token = RetrofitClient.getAuthToken() ?: ""
            val response = apiService.contarItems("Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al contar items"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Calcular total
     */
    suspend fun calcularTotal(): Result<Double> = withContext(Dispatchers.IO) {
        return@withContext try {
            val token = RetrofitClient.getAuthToken() ?: ""
            val response = apiService.calcularTotal("Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al calcular total"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Procesar compra
     */
    suspend fun procesarCompra(): Result<CompraResponse> = withContext(Dispatchers.IO) {
        return@withContext try {
            val token = RetrofitClient.getAuthToken() ?: ""
            val response = apiService.procesarCompra("Bearer $token")

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Error al procesar compra"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}