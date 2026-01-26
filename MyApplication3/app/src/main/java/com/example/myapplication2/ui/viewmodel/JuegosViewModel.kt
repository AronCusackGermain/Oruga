package com.example.myapplication.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.remote.dtos.JuegoDto
import com.example.myapplication.domain.repository.OrugaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class JuegosUiState(
    val juegos: List<JuegoDto> = emptyList(),
    val juegoSeleccionado: JuegoDto? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel para catálogo de juegos - SOLO BACKEND
 */
class JuegosViewModel : ViewModel() {

    private val repository = OrugaRepository()

    private val _uiState = MutableStateFlow(JuegosUiState())
    val uiState: StateFlow<JuegosUiState> = _uiState.asStateFlow()

    init {
        cargarJuegos()
    }

    /**
     * Cargar todos los juegos desde el backend
     */
    fun cargarJuegos() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.getJuegos()
                .onSuccess { juegos ->
                    Log.d("JuegosViewModel", "✅ Juegos cargados: ${juegos.size}")
                    _uiState.value = _uiState.value.copy(
                        juegos = juegos,
                        isLoading = false
                    )
                }
                .onFailure { error ->
                    Log.e("JuegosViewModel", "❌ Error al cargar juegos", error)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Error desconocido"
                    )
                }
        }
    }

    /**
     * Cargar juego por ID
     */
    fun cargarJuegoPorId(id: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            repository.getJuegoPorId(id)
                .onSuccess { juego ->
                    _uiState.value = _uiState.value.copy(
                        juegoSeleccionado = juego,
                        isLoading = false
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }

    /**
     * Buscar juegos por género
     */
    fun buscarPorGenero(genero: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.buscarJuegosPorGenero(genero)
                .onSuccess { juegos ->
                    _uiState.value = _uiState.value.copy(
                        juegos = juegos,
                        isLoading = false
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message
                    )
                }
        }
    }

    /**
     * Obtener juego por ID (de la lista cargada)
     */
    fun obtenerJuegoPorId(id: Long): JuegoDto? {
        return _uiState.value.juegos.find { it.id == id }
    }
}