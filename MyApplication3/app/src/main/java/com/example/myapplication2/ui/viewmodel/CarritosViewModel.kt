package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.dtos.CarritoItemDto
import com.example.myapplication.data.remote.dtos.CarritoResponseDto
import com.example.myapplication.domain.repository.OrugaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para carrito de compras - SOLO BACKEND
 */
class CarritoViewModel : ViewModel() {

    private val repository = OrugaRepository()

    private val _carritoResponse = MutableStateFlow<CarritoResponseDto?>(null)
    val carritoResponse: StateFlow<CarritoResponseDto?> = _carritoResponse.asStateFlow()

    private val _itemsCarrito = MutableStateFlow<List<CarritoItemDto>>(emptyList())
    val itemsCarrito: StateFlow<List<CarritoItemDto>> = _itemsCarrito.asStateFlow()

    private val _cantidadItems = MutableStateFlow(0)
    val cantidadItems: StateFlow<Int> = _cantidadItems.asStateFlow()

    private val _totalCarrito = MutableStateFlow(0.0)
    val totalCarrito: StateFlow<Double> = _totalCarrito.asStateFlow()

    /**
     * Cargar carrito desde backend
     */
    fun cargarCarrito(token: String) {
        viewModelScope.launch {
            repository.getCarrito(token)
                .onSuccess { carrito ->
                    _carritoResponse.value = carrito
                    _itemsCarrito.value = carrito.items
                    _cantidadItems.value = carrito.cantidadItems
                    _totalCarrito.value = carrito.total
                }
                .onFailure { error ->
                    // Carrito vacío o error
                    _itemsCarrito.value = emptyList()
                    _cantidadItems.value = 0
                    _totalCarrito.value = 0.0
                }
        }
    }

    /**
     * Agregar juego al carrito
     */
    fun agregarAlCarrito(
        token: String,
        juegoId: Long,
        cantidad: Int = 1,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            repository.agregarAlCarrito(token, juegoId, cantidad)
                .onSuccess { carrito ->
                    _carritoResponse.value = carrito
                    _itemsCarrito.value = carrito.items
                    _cantidadItems.value = carrito.cantidadItems
                    _totalCarrito.value = carrito.total
                    onResult(true, "✅ Agregado al carrito")
                }
                .onFailure { error ->
                    onResult(false, error.message ?: "Error al agregar")
                }
        }
    }

    /**
     * Vaciar carrito
     */
    fun vaciarCarrito(token: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            repository.vaciarCarrito(token)
                .onSuccess {
                    _itemsCarrito.value = emptyList()
                    _cantidadItems.value = 0
                    _totalCarrito.value = 0.0
                    _carritoResponse.value = null
                    onResult(true, "Carrito vaciado")
                }
                .onFailure { error ->
                    onResult(false, error.message ?: "Error")
                }
        }
    }

    /**
     * Procesar compra (iniciar checkout)
     */
    fun procesarCompra(
        token: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            if (_itemsCarrito.value.isEmpty()) {
                onResult(false, "El carrito está vacío")
                return@launch
            }

            repository.iniciarCheckout(token)
                .onSuccess { checkoutResponse ->
                    // Limpiar carrito local
                    _itemsCarrito.value = emptyList()
                    _cantidadItems.value = 0
                    _totalCarrito.value = 0.0
                    _carritoResponse.value = null

                    onResult(
                        true,
                        "✅ Orden creada: ${checkoutResponse.orden.numeroOrden}\nTotal: ${checkoutResponse.orden.totalFormateado}"
                    )
                }
                .onFailure { error ->
                    onResult(false, error.message ?: "Error al procesar compra")
                }
        }
    }
}