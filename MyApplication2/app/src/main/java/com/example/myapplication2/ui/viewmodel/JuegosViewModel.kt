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
    val isLoading: Boolean = false,
    val error: String? = null
)

class JuegosViewModel(
    private val repository: OrugaRepository = OrugaRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(JuegosUiState())
    val uiState: StateFlow<JuegosUiState> = _uiState.asStateFlow()

    init {
        cargarJuegos()
    }

    fun cargarJuegos() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.getJuegos()
                .onSuccess { juegos ->
                    Log.d("JuegosViewModel", "Juegos cargados: ${juegos.size}")
                    _uiState.value = _uiState.value.copy(
                        juegos = juegos,
                        isLoading = false
                    )
                }
                .onFailure { error ->
                    Log.e("JuegosViewModel", "Error al cargar juegos", error)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Error desconocido"
                    )
                }
        }
    }

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
}
