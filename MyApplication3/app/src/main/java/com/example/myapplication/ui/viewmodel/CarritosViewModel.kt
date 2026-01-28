package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.dto.ItemCarritoResponse
import com.example.myapplication.domain.repository.CarritoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CarritoViewModel(private val repository: CarritoRepository) : ViewModel() {

    private val _itemsCarrito = MutableStateFlow<List<ItemCarritoResponse>>(emptyList())
    val itemsCarrito: StateFlow<List<ItemCarritoResponse>> = _itemsCarrito.asStateFlow()

    private val _cantidadItems = MutableStateFlow(0L)
    val cantidadItems: StateFlow<Long> = _cantidadItems.asStateFlow()

    private val _totalCarrito = MutableStateFlow(0.0)
    val totalCarrito: StateFlow<Double> = _totalCarrito.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * Cargar carrito
     */
    fun cargarCarrito(usuarioId: Long) {
        viewModelScope.launch {
            _isLoading.value = true

            // Obtener items
            val resultItems = repository.obtenerCarrito()
            resultItems.onSuccess { items ->
                _itemsCarrito.value = items
            }

            // Contar items
            val resultCantidad = repository.contarItems()
            resultCantidad.onSuccess { cantidad ->
                _cantidadItems.value = cantidad
            }

            // Calcular total
            val resultTotal = repository.calcularTotal()
            resultTotal.onSuccess { total ->
                _totalCarrito.value = total
            }

            _isLoading.value = false
        }
    }

    /**
     * Agregar al carrito
     */
    fun agregarAlCarrito(
        usuarioId: Long,
        juegoId: Int,
        nombreJuego: String,
        precio: Double,
        imagenUrl: Int,
        cantidad: Int = 1,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.agregarAlCarrito(
                juegoId, nombreJuego, precio, imagenUrl, cantidad
            )

            result.fold(
                onSuccess = {
                    cargarCarrito(usuarioId)
                    onResult(true, "✅ Agregado al carrito")
                },
                onFailure = { error ->
                    onResult(false, error.message ?: "Error al agregar")
                }
            )
        }
    }

    /**
     * Actualizar cantidad
     */
    fun actualizarCantidad(
        itemId: Long,
        cantidad: Int,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.actualizarCantidad(itemId, cantidad)

            result.fold(
                onSuccess = {
                    cargarCarrito(0L) // Recargar
                    onResult(true, "Cantidad actualizada")
                },
                onFailure = { error ->
                    onResult(false, error.message ?: "Error")
                }
            )
        }
    }

    /**
     * Eliminar item
     */
    fun eliminarItem(item: ItemCarritoResponse, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.eliminarItem(item.id)

            result.fold(
                onSuccess = {
                    cargarCarrito(0L)
                    onResult(true, "Eliminado del carrito")
                },
                onFailure = { error ->
                    onResult(false, error.message ?: "Error")
                }
            )
        }
    }

    /**
     * Vaciar carrito
     */
    fun vaciarCarrito(usuarioId: Long, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.vaciarCarrito()

            result.fold(
                onSuccess = {
                    cargarCarrito(usuarioId)
                    onResult(true, "Carrito vaciado")
                },
                onFailure = { error ->
                    onResult(false, error.message ?: "Error")
                }
            )
        }
    }

    /**
     * Procesar compra
     */
    fun procesarCompra(usuarioId: Long, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.procesarCompra()

            result.fold(
                onSuccess = { compra ->
                    cargarCarrito(usuarioId)
                    onResult(true, "🎉 ${compra.mensaje} Total: $${String.format("%,.0f", compra.total)}")
                },
                onFailure = { error ->
                    onResult(false, error.message ?: "Error al procesar compra")
                }
            )
        }
    }
}

/**
 * Factory para CarritoViewModel
 */
class CarritoViewModelFactory(
    private val repository: CarritoRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CarritoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CarritoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}