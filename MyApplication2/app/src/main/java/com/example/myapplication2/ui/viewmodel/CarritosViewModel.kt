package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.models.ItemCarrito
import com.example.myapplication.domain.repository.CarritoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para gestionar el carrito de compras
 */
class CarritoViewModel(private val repository: CarritoRepository) : ViewModel() {

    private val _itemsCarrito = MutableStateFlow<List<ItemCarrito>>(emptyList())
    val itemsCarrito: StateFlow<List<ItemCarrito>> = _itemsCarrito.asStateFlow()

    private val _cantidadItems = MutableStateFlow(0)
    val cantidadItems: StateFlow<Int> = _cantidadItems.asStateFlow()

    private val _totalCarrito = MutableStateFlow(0.0)
    val totalCarrito: StateFlow<Double> = _totalCarrito.asStateFlow()

    /**
     * Cargar carrito del usuario
     */
    fun cargarCarrito(usuarioId: Int) {
        viewModelScope.launch {
            repository.obtenerCarrito(usuarioId).collect { items ->
                _itemsCarrito.value = items
                _totalCarrito.value = repository.calcularTotal(items)
            }
        }

        viewModelScope.launch {
            repository.contarItems(usuarioId).collect { count ->
                _cantidadItems.value = count
            }
        }
    }

    /**
     * Agregar juego al carrito
     */
    fun agregarAlCarrito(
        usuarioId: Int,
        juegoId: Int,
        nombreJuego: String,
        precio: Double,
        imagenUrl: Int,
        cantidad: Int = 1,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.agregarAlCarrito(
                usuarioId, juegoId, nombreJuego, precio, imagenUrl, cantidad
            )
            result.fold(
                onSuccess = {
                    onResult(true, "Agregado al carrito")
                },
                onFailure = { error ->
                    onResult(false, error.message ?: "Error al agregar")
                }
            )
        }
    }

    /**
     * Actualizar cantidad de un item
     */
    fun actualizarCantidad(
        itemId: Int,
        cantidad: Int,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repository.actualizarCantidad(itemId, cantidad)
            result.fold(
                onSuccess = { onResult(true, "Cantidad actualizada") },
                onFailure = { error -> onResult(false, error.message ?: "Error") }
            )
        }
    }

    /**
     * Eliminar item del carrito
     */
    fun eliminarItem(item: ItemCarrito, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.eliminarDelCarrito(item)
            result.fold(
                onSuccess = { onResult(true, "Eliminado del carrito") },
                onFailure = { error -> onResult(false, error.message ?: "Error") }
            )
        }
    }

    /**
     * Vaciar carrito completo
     */
    fun vaciarCarrito(usuarioId: Int, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = repository.vaciarCarrito(usuarioId)
            result.fold(
                onSuccess = { onResult(true, "Carrito vaciado") },
                onFailure = { error -> onResult(false, error.message ?: "Error") }
            )
        }
    }

    /**
     * Procesar compra (simulación)
     */
    fun procesarCompra(
        usuarioId: Int,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            if (_itemsCarrito.value.isEmpty()) {
                onResult(false, "El carrito está vacío")
                return@launch
            }

            // Aquí irían los procesos reales de pago
            // Por ahora solo vaciamos el carrito
            val result = repository.vaciarCarrito(usuarioId)
            result.fold(
                onSuccess = {
                    onResult(true, "¡Compra realizada con éxito! Total: $${String.format("%,.0f", _totalCarrito.value).replace(",", ".")}")
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
